package org.airbloc.airbridgecustomtest;

import android.content.Intent;
import android.net.Uri;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import org.json.JSONObject;

import io.airbridge.AirBridge;
import io.airbridge.deeplink.DeepLink;
import io.airbridge.statistics.Tracker;
import io.airbridge.statistics.events.AppShutdownEvent;
import io.airbridge.statistics.events.DeepLinkLaunchEvent;
import io.airbridge.statistics.events.FirebaseDeeplinkEvent;

public class HomeActivtiy extends AppCompatActivity {

    Button webview_bt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activtiy);

        webview_bt=(Button)findViewById(R.id.home_webview_button);
        webview_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intet=new Intent(HomeActivtiy.this,WebviewActivity.class);
                startActivity(intet);
            }
        });


        if(getIntent()!=null){
            String redirectUrl;
            if(getIntent().getStringExtra("airbridgeLink")!=null){
                redirectUrl=getIntent().getStringExtra("airbridgeLink");
                Log.d(Config.TAG,"received PushMessage Data : "+redirectUrl);
                String basicUrl=redirectUrl.substring(redirectUrl.indexOf("link=")+5,redirectUrl.indexOf("&product"));
                String productId=redirectUrl.substring(redirectUrl.indexOf("product_id")+10,redirectUrl.indexOf("&airbridge"));
                //TODO public void InAppTouchPointEvent();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("customtest://webview?value="+basicUrl));
                intent.putExtra("from","pushMessage");
                startActivity(intent);

            }

        }

        /**
         * Firebase Dynamic Link Branch
         */
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        if(pendingDynamicLinkData!=null){

                            Uri dynamicLink=pendingDynamicLinkData.getLink();
                            Log.d(Config.TAG,"get DynamicLinks : "+dynamicLink.toString());

                            String deeplink=dynamicLink.toString();
                            if(deeplink.contains("webPage")){
                                deeplink=deeplink.substring(deeplink.lastIndexOf("webPage=")+8);
                                Log.d(Config.TAG,"Parsed Deeplink : "+deeplink);
                                deeplink="customtest://webview?value="+deeplink;
                            }
                            AirBridge.getTracker().sendEvent(new FirebaseDeeplinkEvent(String.valueOf(deeplink)));
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setData(Uri.parse(deeplink));
                            intent.putExtra("from","firebase");
                            startActivity(intent);
                        }else{
                            Log.d(Config.TAG,"DynamicLinks is null");
                        }

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(Config.TAG,"fail to get DynamicLinks");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AirBridge.getTracker().setManual(true);
        AirBridge.getTracker().call(new AppShutdownEvent(), new Tracker.EventCallback() {
            @Override
            public void done(JSONObject results) throws Exception {
                Process.killProcess(Process.myPid());
            }
        });

    }
}
