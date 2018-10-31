package org.airbloc.airbridgecustomtest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import io.airbridge.AirBridge;

public class DialogActivity extends Activity {

    Button confirm_bt;
    PermissionListener permissionListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        AirBridge.getTracker().delayEvent();

        permissionListener=new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(DialogActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREFERENCE, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isFirst", false);
                editor.apply();

                setResult(Activity.RESULT_OK,null);
                finish();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(DialogActivity.this, "권한 거부", Toast.LENGTH_SHORT).show();
            }
        };


        confirm_bt=(Button)findViewById(R.id.dialog_confirm_button);

        confirm_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TedPermission.with(DialogActivity.this)
                        .setPermissionListener(permissionListener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.READ_PHONE_STATE)
                        .check();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AirBridge.getTracker().restoreEvent();
    }
}
