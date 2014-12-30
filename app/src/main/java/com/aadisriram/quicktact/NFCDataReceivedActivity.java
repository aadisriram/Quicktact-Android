package com.aadisriram.quicktact;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

        TextView twitterUsername = (TextView) findViewById(R.id.twitter_username);
        twitterUsername.setText(sdm.getTwitterId());

        TextView facebookUsername = (TextView) findViewById(R.id.facebook_name);
        facebookUsername.setText(sdm.getFacebookName());

        TextView googlePlusUsername = (TextView) findViewById(R.id.google_plus_username);
        googlePlusUsername.setText(sdm.getGooglePlusUsername());

        LinearLayout facebookLayout = (LinearLayout) findViewById(R.id.facebook_load_profile);
        facebookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = getOpenFacebookIntent(getApplicationContext(), sdm.getFacebookId());
                facebookIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(facebookIntent);
            }
        });

        LinearLayout twitterLayout = (LinearLayout) findViewById(R.id.twitter_load_profile);
        twitterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTwitter(sdm.getTwitterId());
            }
        });

        LinearLayout googlePlusLayout = (LinearLayout) findViewById(R.id.google_plus_load_profile);
        googlePlusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGooglePlus(sdm.getGooglePlusId());
            }
        });
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
