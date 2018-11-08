package org.airbloc.airbridgecustomtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import io.airbridge.AirBridge;
import io.airbridge.deeplink.DeepLink;
import io.airbridge.statistics.events.InAppTouchPointDeeplinkEvent;

public class WebviewActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = (WebView) findViewById(R.id.webview_webview_webview);
//        webView.loadUrl("https://airbridge.io");


        if (DeepLink.hadOpened(this)) {
            String deeplink = getIntent().getDataString();
            if (getIntent().getStringExtra("from").equals("pushMessage")) {
                String redirectUrl = getIntent().getStringExtra("deeplinkUrl");
                if (redirectUrl != null)
                    AirBridge.getTracker().sendEvent(new InAppTouchPointDeeplinkEvent(redirectUrl));

            }


            Log.d(Config.TAG, "WebVeiwActivity is opened with deeplink : " + deeplink);

            if (deeplink.contains("value")) {
                String url = deeplink.substring(deeplink.lastIndexOf("value=") + 6);
                Log.d(Config.TAG, "WebView Url : " + url);
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl(url);
            } else
                Log.d(Config.TAG, "Deeplink doesn't include value parameter");
        }
    }
}
