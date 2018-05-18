package com.example.fx50j.zhihu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.fx50j.zhihu.R;
import com.example.fx50j.zhihu.utils.CharsetStringRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    //知乎日报接口
    private String url = "http://news-at.zhihu.com/api/4/start-image/1080*1776";

    private ImageView mIvSplash;
    private TextView mTvContent;
    private RequestQueue queue;
    private StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        new Thread(){
            @Override
            public void run() {
                SystemClock.sleep(2000);
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    };

    private void init() {
        /*
        隐藏状态栏
        */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        mIvSplash = (ImageView) findViewById(R.id.iv_start_bg);
        mTvContent = (TextView) findViewById(R.id.tv_content);

        /*
        使用Volley请求数据
         */
        queue = Volley.newRequestQueue(this);
        /*
        加载图片
         *///请求失败
        request = new CharsetStringRequest(Request.Method.GET, this.url,
                json -> {
                    try {
                        JSONObject object = new JSONObject(json);

                            mTvContent.setText(object.getString("text"));


                        /*
                        加载图片
                         */
                        Glide.with(this).load(object.getString("img")).into(mIvSplash);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, volleyError -> {//请求失败
            Toast.makeText(this, "网络错误", Toast.LENGTH_SHORT).show();

        });

        queue.add(request);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*
        停止Volley
         */
        queue.stop();
        request.cancel();
    }
}
