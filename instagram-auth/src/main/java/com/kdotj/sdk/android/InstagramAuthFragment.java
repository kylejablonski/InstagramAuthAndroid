package com.kdotj.sdk.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Fragment which goes through the auth flow for instagram
 * <br>
 * Note: Instagram client apps must authenticate via a WebView
 * <br>
 * Once a request is made to the auth endpoint, the redirect url
 * will contain the access code on the URL as a fragment, which we parse
 * off the url and return via a callback to {@link TokenCallback#onTokenReceived(String)}.
 * In order to receive this info, the parent must implement the {@link TokenCallback} interface.
 */

public final class InstagramAuthFragment extends Fragment {

    private static final String TAG = InstagramAuthFragment.class.getSimpleName();

    /*
        Instagram OAuth url and params
     */
    private static final String INSTRAGRAM_API_URL = "api.instagram.com";
    private static final String INSTRAGRAM_URL = "www.instagram.com";
    private static final String BASE_URL = "http://api.instagram.com/oauth/authorize/?";
    private static final String PARAM_CLIENT_ID = "client_id=";
    private static final String PARAM_REDIRECT_URI = "&redirect_uri=";
    private static final String PARAM_RESPONSE_TYPE = "&response_type=token";
    private static final String PARAM_SCOPE = "&scope=";
    private static final String DEFAULT_PROTOCOL = "http://";


    /**
     * Extra for setting the redirect uri required by auth
     */
    public static final String EXTRA_REDIRECT_URI = "redirect_uri";

    /**
     * Client id for the app trying to perform Oauth
     */
    public static final String EXTRA_CLIENT_ID = "client_id";

    /**
     * Extra for passing protocol for redirect uri
     */
    public static final String EXTRA_REDIRECT_PROTOCOL = "protocol";

    /**
     * Extra for passing in the login scope:
     * values: basic, public_content, follower_list, comments, relationships, likes
     * must pass in via String []
     */
    public static final String EXTRA_SCOPES = "scopes";

    /*
        Member Variables
     */
    /**
     * Url to load in the webview
     */
    private String strOauthURL;

    /**
     * Client id from the client app
     */
    private String strClientId;

    /**
     * Redirect Uri from the client app
     */
    private String strRedirectUri;

    /**
     * Redirect Uri protocol
     */
    private String strRedirectProtocol;

    /**
     * Array containing scopes to append onto the URL
     */
    private String [] scopes;

    /**
     * WebView responsible for loading the {@link #strOauthURL}
     * <br/>
     * Note: expose this for Activities that want to support webview
     * navigation
     */
    private WebView mWebView;

    /**
     * Progress when pages are loading
     */
    private ProgressBar mProgressBar;

    /**
     * Interface for telling the parent Activity or Fragment an
     * access token was received
     */
    private TokenCallback mCallback;

    /**
     * New instance method for creating this fragment
     * @param args the args as a bundle
     * @return this
     */
    public static InstagramAuthFragment newInstance(Bundle args){
        InstagramAuthFragment fragment = new InstagramAuthFragment();
        if(args != null){
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mCallback = (TokenCallback) context;
        }catch (ClassCastException ex){
            throw new RuntimeException("Parent must implement TokenCallback interface");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strOauthURL = createAuthUrl(getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instagram_auth, container, false);
        mWebView = (WebView) view.findViewById(R.id.webview_auth);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_loading);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView.setWebViewClient(new InstagramWebViewClient());
        mWebView.loadUrl(strOauthURL);
    }

    /**
     * Gets the WebView instance for navigation in parent
     * @return the current WebView
     */
    public WebView getWebView(){
        return mWebView;
    }

    /**
     * Creates the OAuth url from the values passed into the fragment
     * @param args - the bundle containing the args
     * @return the url for the oauth endpoint to load in the webview
     */
    private String createAuthUrl(Bundle args){
        if(args == null){
            throw new IllegalArgumentException("Must pass in a bundle to the sdk!");
        }
        strClientId = args.getString(EXTRA_CLIENT_ID);
        strRedirectUri = args.getString(EXTRA_REDIRECT_URI);
        strRedirectProtocol = args.getString(EXTRA_REDIRECT_PROTOCOL);
        scopes = args.getStringArray(EXTRA_SCOPES);

        if(TextUtils.isEmpty(strClientId) || TextUtils.isEmpty(strRedirectUri)){
            throw new IllegalArgumentException("Must specify redirect uri & client id to use the SDK");
        }

        StringBuilder stringBuilder = new StringBuilder(BASE_URL);
        stringBuilder.append(PARAM_CLIENT_ID);
        stringBuilder.append(strClientId);
        stringBuilder.append(PARAM_REDIRECT_URI);
        stringBuilder.append(TextUtils.isEmpty(strRedirectProtocol) ? strRedirectProtocol : DEFAULT_PROTOCOL );
        stringBuilder.append(strRedirectUri);
        stringBuilder.append(PARAM_RESPONSE_TYPE);

        // Add the scope to the login
        if(scopes != null && scopes.length > 0) {
            stringBuilder.append(PARAM_SCOPE);
            for (int i = 0; i < scopes.length; i++) {
                String scope = scopes[i];
                stringBuilder.append(scope);
                if(i != scopes.length - 1){
                    stringBuilder.append("+");
                }
            }
        }

        return stringBuilder.toString();

    }

    /**
     * WebViewClient for overriding url loading
     */
    private class InstagramWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(TAG, "onPageStarted() called with: view = [" + view + "], url = [" + url + "], favicon = [" + favicon + "]");
            mProgressBar.setIndeterminate(true);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, "onPageFinished() called with: view = [" + view + "], url = [" + url + "]");
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onFormResubmission(WebView view, Message dontResend, Message resend) {
            super.onFormResubmission(view, dontResend, resend);
            Log.d(TAG, "onFormResubmission() called with: view = [" + view + "], dontResend = [" + dontResend + "], resend = [" + resend + "]");
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "shouldOverrideUrlLoading() called with: view = [" + view + "], url = [" + url + "]");
            if (Uri.parse(url).getHost().equals(INSTRAGRAM_API_URL)
                    || Uri.parse(url).getHost().equals(INSTRAGRAM_URL)) {
                // don't leave the app for instagram urls
                return false;
            }else if(!TextUtils.isEmpty(strRedirectUri) && Uri.parse(url).getHost().equals(strRedirectUri)){
                // parse the url and pull off the fragment with the accessToken
                String accessToken = url.substring(url.indexOf("=") + 1);
                mCallback.onTokenReceived(accessToken);
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

}
