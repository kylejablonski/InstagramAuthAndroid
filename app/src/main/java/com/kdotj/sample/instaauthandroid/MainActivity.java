package com.kdotj.sample.instaauthandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.widget.Toast;

import com.kdotj.sdk.android.InstagramAuthFragment;
import com.kdotj.sdk.android.TokenCallback;

public class MainActivity extends AppCompatActivity implements TokenCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    // CLIENT ID from instagram
    String clientId = "<YOUR_API_KEY_HERE>";

    // Your website
    String redirectUri = "<YOUR_WEBSITE_REDIRECT_URI>";

    // website http:// or https://
    String redirectProtocol = "<http:// or https://>";

    // Scopes for the login see https://www.instagram.com/developer/authorization/
    String [] scopes = new String [] {
            "basic", // to read a user’s profile info and media
            "public_content", // to read any public profile info and media on a user’s behalf
            "follower_list", // to read the list of followers and followed-by users
            "comments", // to post and delete comments on a user’s behalf
            "relationships", // to follow and unfollow accounts on a user’s behalf
            "likes" // to like and unlike media on a user’s behalf
    };

    private AppCompatTextView mTvStatus;
    public InstagramAuthFragment instagramAuthFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvStatus = (AppCompatTextView) findViewById(R.id.tv_status);


        /*
            Load a new InstagramAuthFragment
         */
        Bundle args = new Bundle();
        args.putString(InstagramAuthFragment.EXTRA_CLIENT_ID, clientId);
        args.putString(InstagramAuthFragment.EXTRA_REDIRECT_URI, redirectUri);
        args.putString(InstagramAuthFragment.EXTRA_REDIRECT_PROTOCOL, redirectProtocol);
        args.putStringArray(InstagramAuthFragment.EXTRA_SCOPES, scopes);
        instagramAuthFragment = InstagramAuthFragment.newInstance(args);
        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_container, instagramAuthFragment, InstagramAuthFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {

        /*
            Handle the navigation in the webview of the instagram fragment
         */
        if(instagramAuthFragment != null && instagramAuthFragment.getWebView() != null){
            if(instagramAuthFragment.getWebView().canGoBack()){
                instagramAuthFragment.getWebView().goBack();
            }else{
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }
    }


    /**
     * Override this method to receive the access token after sign in
     * @param accessToken - the access token
     */
    @Override
    public void onTokenReceived(String accessToken) {
        Log.d(TAG, "Token received: "+ accessToken);
        Toast.makeText(MainActivity.this, accessToken, Toast.LENGTH_SHORT).show();

        instagramAuthFragment = (InstagramAuthFragment) getSupportFragmentManager().findFragmentByTag(InstagramAuthFragment.class.getSimpleName());
        if(instagramAuthFragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(instagramAuthFragment)
                    .commit();
        }

        mTvStatus.setText(String.format("Logged in: %s", accessToken));
    }

}
