package org.airbloc.airbridgecustomtest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.airbridge.AirBridge;

public class IntroActivity extends AppCompatActivity {
    Handler handler;
    Runnable r;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        init();

        AirBridge.getTracker().delayEvent();
        AirBridge.getTracker().restoreEvent();

    }

    @Override
    protected void onResume() {
        super.onResume();

        handler.postDelayed(r, 2000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(r);
    }

    public void init() {
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {

                if (isFirst()) {
                    Intent intent = new Intent(IntroActivity.this, DialogActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    // 4초뒤에 다음화면(MainActivity)으로 넘어가기 Handler 사용
                    Intent intent = new Intent(getApplicationContext(), HomeActivtiy.class);
                    startActivity(intent); // 다음화면으로 넘어가기
                    finish(); // Activity 화면 제거
                }

            }
        };
        sharedPreferences = getSharedPreferences(Config.SHARED_PREFERENCE, MODE_PRIVATE);


    }

    public Boolean isFirst() {
        return sharedPreferences.getBoolean("isFirst", true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Intent intent1 = new Intent(IntroActivity.this, HomeActivtiy.class);

                    startActivity(intent1);
                    finish();
                    break;
                }
        }
    }

}
