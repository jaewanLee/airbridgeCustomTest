package org.airbloc.airbridgecustomtest;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import io.airbridge.AirBridge;
import io.airbridge.deeplink.DeepLink;
import io.airbridge.statistics.events.FirebaseDeeplinkEvent;
import io.airbridge.statistics.events.InAppTouchPointDeeplinkEvent;

public class WebviewActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = (WebView) findViewById(R.id.webview_webview_webview);
//        webView.loadUrl("https://airbridge.io");
        //SingleTask를 항상 먼지 실행시켜서 onNEwIntent 실행여부를 테스트함
        if(getIntent()!=null){
            if(!getIntent().getBooleanExtra("isRestart",false)){
                Log.d(Config.TAG,"restart");
                startActivity(new Intent(this,HomeActivtiy.class).putExtra("needRestart",true));
            }
        }


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


                //Firebase중복 Data요청 테스트
                firebaseDynamicLinkcallback(new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        if (pendingDynamicLinkData != null) {

                            Uri dynamicLink = pendingDynamicLinkData.getLink();
                            Log.d(Config.TAG, "get DynamicLinks : " + dynamicLink.toString());

                            String deeplink = dynamicLink.toString();
                            if (deeplink.contains("webPage")) {
                                deeplink = deeplink.substring(deeplink.lastIndexOf("webPage=") + 8);
                                Log.d(Config.TAG, "Parsed Deeplink : " + deeplink);
                                deeplink = "customtest://webview?value=" + deeplink;
                            }
                            AirBridge.getTracker().sendEvent(new FirebaseDeeplinkEvent(String.valueOf(dynamicLink)));

                            Log.d(Config.TAG, "get DynamicLinks : " + dynamicLink.toString());

                        } else {
                            Log.d(Config.TAG, "DynamicLinks is null");
                        }
                    }
                });
            } else
                Log.d(Config.TAG, "Deeplink doesn't include value parameter");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(Config.TAG,"onNewIntent is occured on WebViewActivity");
        AirBridge.getTracker().onNewIntent(intent);

    }
    private void firebaseDynamicLinkcallback(OnSuccessListener<PendingDynamicLinkData> onSuccessListener){
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, onSuccessListener)
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(Config.TAG, "fail to get DynamicLinks");
                    }
                });
    }
}
