package com.aadisriram.quicktact.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aadisriram.quicktact.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

public class TwitterFragment extends Fragment {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "SUvNN3urBPvCFDIZiBwwL4k88";
    private static final String TWITTER_SECRET = "2XlgaD2FakUEo9T1l9NLw4kIgUgHQI3Dqw7MKCBPkMser7OuaP";

    public static String twitterUserId = "";

    private TwitterLoginButton loginButton;
    private LinearLayout logoutButtonLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(getActivity(), new Twitter(authConfig));
        View view = inflater.inflate(R.layout.twitter_fragment, container, false);

        logoutButtonLayout = (LinearLayout) view.findViewById(R.id.twitter_logout_layout);
        logoutButtonLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                SessionManager currentSession = Twitter.getSessionManager();
                if(currentSession != null) {
                    Log.d("Twitter", "Logged out of twitter");
                    Twitter.logOut();
                    twitterUserId = "";
                    loginButton.setVisibility(View.VISIBLE);
                    logoutButtonLayout.setVisibility(View.GONE);
                }
            }
        });

        loginButton = (TwitterLoginButton) view.findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d("Twitter", "Logged into twitter");
                loginButton.setVisibility(View.GONE);
                logoutButtonLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    public static void getTwitterUserId() {
        twitterUserId = Twitter.getSessionManager().getActiveSession().getUserName();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Twitter.getSessionManager().getActiveSession() != null) {
            getTwitterUserId();
            loginButton.setVisibility(View.GONE);
            logoutButtonLayout.setVisibility(View.VISIBLE);
        }
    }
}
