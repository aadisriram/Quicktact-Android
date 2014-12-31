package com.aadisriram.quicktact;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.content.Intent;
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
    AlertDialog.Builder nfcAlertBox;
    AlertDialog nfcAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewRelic.withApplicationToken(
                "AA566a84bda48f5c8011c5b0b664b9bc73027bef4a"
        ).start(this.getApplication());

        setContentView(R.layout.activity_main);
        nfcAlertBox = new AlertDialog.Builder(MainActivity.this);
        setupAlert();
        nfcAlertDialog = nfcAlertBox.create();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {
            showNFCAlert();
        }
        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == GooglePlusFragment.RC_SIGN_IN) {
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
                    TwitterFragment.getTwitterUserId();
                }
            }
        } catch(Exception ex) {
            //TODO: This is a stupid hack, fix it
            Log.d("AuthCallback", "Failed due to bad auth");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action ba
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        SocialDataManager dataManager = new SocialDataManager(
                0,
                FacebookFragment.userID,
                TwitterFragment.twitterUserId,
                FacebookFragment.userName,
                GooglePlusFragment.userId,
                GooglePlusFragment.userName);

        Gson gson = new Gson();

        String text = gson.toJson(dataManager);
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMime(
                        "application/com.aadisriram.quicktact", text.getBytes())
                        ,NdefRecord.createApplicationRecord("com.aadisriram.quicktact")
                });
        Log.d("NFCMessage", gson.toJson(msg));
        return msg;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!mNfcAdapter.isEnabled())
            showNFCAlert();
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

    private void showNFCAlert() {
        if (!mNfcAdapter.isEnabled()) {
            if(!nfcAlertDialog.isShowing()) {
                nfcAlertDialog.setCancelable(false);
                nfcAlertDialog.setCanceledOnTouchOutside(false);
                nfcAlertDialog.show();
            }
        }
    }

    private void setupAlert() {
        nfcAlertBox.setTitle("Quicktact");
        nfcAlertBox.setMessage("Please switch on NFC");
        nfcAlertBox.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(intent);
                }
            }
        });
        nfcAlertBox.setNegativeButton("Close", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }
}
