package com.aadisriram.quicktact;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.aadisriram.quicktact.fragments.MainFragment;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import static android.nfc.NdefRecord.createMime;


public class MainActivity extends FragmentActivity implements NfcAdapter.CreateNdefMessageCallback {

    private UiLifecycleHelper uiHelper;
    private MainFragment mainFragment;
    NfcAdapter mNfcAdapter;
    protected boolean intentProcessed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mainFragment = new MainFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mainFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            mainFragment = (MainFragment) this.getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }

    public static Intent getOpenFacebookIntent(Context context, String userId) {

//        try {
//            context.getPackageManager()
//                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
//            return new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("fb://profile/"+userId)); //Trys to make intent with FB's URI
//        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse(userId)); //catches and opens a url to the desired page
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_LONG).show();
        } else if (state.isClosed()) {
            Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_LONG).show();
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        String message = "Please ask your friend login into facebook";

        if(MainFragment.userID != null)
            message = MainFragment.userID;

        String text = (message);
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMime(
                        "application/vnd.com.aadisriram.quicktact", text.getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                         */
                        //,NdefRecord.createApplicationRecord("com.example.android.beam")
                });
        return msg;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if(!intentProcessed) {
            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
                intentProcessed = true;
                processIntent(getIntent());
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

        // onResume gets called after this to handle the intent
        intentProcessed = false;
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        String userId = new String(msg.getRecords()[0].getPayload());
        Toast.makeText(getApplication(), userId, Toast.LENGTH_LONG).show();
        Intent facebookIntent = getOpenFacebookIntent(getApplicationContext(), userId);
        facebookIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(facebookIntent);
    }

}
