package com.kdotj.sdk.android;

/**
 * Interface to communicate the Authentication token
 * from the Instagram Api
 * Created by kyle.jablonski on 3/30/17.
 */

public interface TokenCallback {


    /**
     * Callback when a token is received
     * @param accessToken the token from the auth response
     */
    void onTokenReceived(String accessToken);
}
