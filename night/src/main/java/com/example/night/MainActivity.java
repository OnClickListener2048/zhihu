package com.example.night;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private WindowManager mWindowManager;
    private View myView;
    private Button btn_dayAndnight;
    private SharedPreferences skinSp;
    private final static String DAY = "day";
    private final static String NIGHT = "night";
    private int flage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        skinSp = this.getSharedPreferences("skinchange", Context.MODE_PRIVATE);
        btn_dayAndnight = (Button) findViewById(R.id.btn_dayAndnight);
        btn_dayAndnight.setOnClickListener(this);

        String mode = skinSp.getString("skin", "");
        if (mode != null || !mode.equals("")) {
            if (mode.equals(NIGHT)) {
                night();
            } else {
                day();
            }
        }

    }

    public void onClick(View v) {
        if (flage % 2 == 0) {
            night();
            btn_dayAndnight.setText("白天模式");
            btn_dayAndnight.setTextColor(Color.WHITE);
            flage++;
        } else {
            day();
            btn_dayAndnight.setText("夜间模式");
            btn_dayAndnight.setTextColor(Color.BLACK);
            flage++;
        }
    }

    public void night() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,
                LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM;
        params.y = 10;
        if (myView == null) {
            myView = new TextView(this);
            myView.setBackgroundColor(0x80000000);
        }
        mWindowManager.addView(myView, params);
        Editor edit = skinSp.edit();
        edit.putString("skin", NIGHT);
        edit.commit();
    }

    public void day() {
        if (myView != null) {
            mWindowManager.removeView(myView);
            Editor edit = skinSp.edit();
            edit.putString("skin", DAY);
            edit.commit();
        }
    }


    public void removeSkin() {
        if (myView != null) {
            mWindowManager.removeView(myView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String mode = skinSp.getString("skin", "");
        if (mode.equals(NIGHT)) {
            removeSkin();
        }
    }
}
