package com.aadisriram.quicktact;

import android.support.v4.app.Fragment;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.aadisriram.quicktact.DataClasses.SocialDataManager;
import com.aadisriram.quicktact.fragments.FacebookFragment;
import com.aadisriram.quicktact.fragments.GooglePlusFragment;
import com.aadisriram.quicktact.fragments.TwitterFragment;

import com.google.gson.Gson;

import static android.nfc.NdefRecord.createMime;

import com.newrelic.agent.android.NewRelic;

public class MainActivity extends FragmentActivity implements NfcAdapter.CreateNdefMessageCallback {

    NfcAdapter mNfcAdapter;
    protected boolean intentProcessed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewRelic.withApplicationToken(
                "AA566a84bda48f5c8011c5b0b664b9bc73027bef4a"
        ).start(this.getApplication());

        setContentView(R.layout.activity_main);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GooglePlusFragment.RC_SIGN_IN) {
            Fragment fragment = getSupportFragmentManager()
                    .findFragmentById(R.id.google_plus_fragment);
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            Fragment fragment = getSupportFragmentManager()
                    .findFragmentById(R.id.twitter_fragment);
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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
        SocialDataManager dataManager = new SocialDataManager(0, FacebookFragment.userID,
                TwitterFragment.getTwitterUserId(),
                FacebookFragment.userName,
                GooglePlusFragment.userId,
                GooglePlusFragment.userName);

        Gson gson = new Gson();

        String text = gson.toJson(dataManager);
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
                        //,NdefRecord.createApplicationRecord("com.aadisriram.quicktact")
                });
        Log.d("NFCMessage", gson.toJson(msg));
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

    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        String userId = new String(msg.getRecords()[0].getPayload());
        Intent nfcRecIntent = new Intent(this, NFCDataReceivedActivity.class);
        nfcRecIntent.putExtra("data_string", userId);
        startActivity(nfcRecIntent);
    }
}
