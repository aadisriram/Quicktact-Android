package com.aadisriram.quicktact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aadisriram.quicktact.DataClasses.SocialDataManager;
import com.google.gson.Gson;


public class NFCDataReceivedActivity extends Activity {

    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcdata_received);

        gson = new Gson();

        Intent intent = getIntent();
        String dataString = intent.getStringExtra("data_string");
        final SocialDataManager sdm = gson.fromJson(dataString, SocialDataManager.class);

        final LinearLayout twitterLayout = (LinearLayout) findViewById(R.id.twitter_load_profile);
        final LinearLayout facebookLayout = (LinearLayout) findViewById(R.id.facebook_load_profile);
        final LinearLayout googlePlusLayout = (LinearLayout) findViewById(R.id.google_plus_load_profile);

        final TextView twitterUsername = (TextView) findViewById(R.id.twitter_username);
        String twitterUserString = sdm.getTwitterId();
        if(twitterUserString.length() < 1) {
            twitterLayout.setVisibility(View.GONE);
        } else {
            twitterUsername.setText(sdm.getTwitterId());
            twitterLayout.setVisibility(View.VISIBLE);
            twitterLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openTwitter(sdm.getTwitterId());
                }
            });
        }

        TextView facebookUsername = (TextView) findViewById(R.id.facebook_name);
        String facebookNameString = sdm.getFacebookName();
        if(facebookNameString.length() < 1) {
            facebookLayout.setVisibility(View.GONE);
        } else {
            facebookUsername.setText(sdm.getFacebookName());
            facebookLayout.setVisibility(View.VISIBLE);
            facebookLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent facebookIntent = getOpenFacebookIntent(getApplicationContext(), sdm.getFacebookId());
                    facebookIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(facebookIntent);
                }
            });
        }

        TextView googlePlusUsername = (TextView) findViewById(R.id.google_plus_username);
        String googlePlusNameString = sdm.getGooglePlusUsername();
        if(googlePlusNameString.length() < 1) {
            googlePlusLayout.setVisibility(View.GONE);
        } else {
            googlePlusUsername.setText(sdm.getGooglePlusUsername());
            googlePlusLayout.setVisibility(View.VISIBLE);
            googlePlusLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGooglePlus(sdm.getGooglePlusId());
                }
            });
        }

        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        adapter.setNdefPushMessage(null, this, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nfcdata_received, menu);
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

    private void openTwitter(String twitterUserName) {
        if(twitterUserName != null) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + twitterUserName)));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + twitterUserName)));
            }
        }
    }

    public Intent getOpenFacebookIntent(Context context, String userId) {

        return new Intent(Intent.ACTION_VIEW,
                Uri.parse(userId)); //catches and opens a url to the desired page
    }

    public void openGooglePlus(String userId) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/"+userId+"/posts")));
    }
}
