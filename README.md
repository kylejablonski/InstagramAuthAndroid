# InstagramAuthAndroid
A wrapper library for logging into instagram via the Client side authorization flow on Android

[ ![Download](https://api.bintray.com/packages/kylejablonski/maven/instagram-auth/images/download.svg) ](https://bintray.com/kylejablonski/maven/instagram-auth/_latestVersion)

Features:

- Includes a Fragment named InstagramAuthFragment, which hosts a WebView and facilates authorizing with Instagram on Android in an easy way.
- Once, credentials are accepted on Instagram's authorize endpoint, a callback is invoked which passes the access_token back for all subsequent api calls to instagram's api


Usage:

1. add to your top-level `build.gradle`


```gradle
allprojects {
    repositories {
        jcenter() 
    }
}
```

2. add to the dependencies section of project `build.gradle`

```gradle
dependencies {
   // includes the .aar file from the repository
    compile 'com.kdotj.sdk.android:instagram-auth:X.X.X@aar'

}
```

Sample code:

```java
public class MainActivity extends AppCompatActivity implements TokenCallback{


    private InstagramAuthFragment instagramAuthFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
            Create a bundle and pass the following keys into the InstagramAuthFragment
            For an API key to instagram visit: https://www.instagram.com/developer/clients/manage/
        */
        Bundle args = new Bundle();
        args.putString(InstagramAuthFragment.EXTRA_CLIENT_ID, "1234123412341234234323"); // client id from instagram
        args.putString(InstagramAuthFragment.EXTRA_REDIRECT_URI, "www.google.com"); // your website uri
        args.putString(InstagramAuthFragment.EXTRA_REDIRECT_PROTOCOL, "http://"); // protocol for your website
        instagramAuthFragment = InstagramAuthFragment.newInstance(args);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_container, instagramAuthFragment, InstagramAuthFragment.class.getSimpleName())
                .commit();
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

    @Override
    public void onTokenReceived(String accessToken) {
        Toast.makeText(MainActivity.this, "Token: "+ accessToken, Toast.LENGTH_SHORT).show();
    }
}
```

Copyright 2017 Kyle Jablonski

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
