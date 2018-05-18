package com.xuyongjun.qqlogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String APPID = "222222";

    private Tencent mTencent;
    private UserInfo mUserInfo;

    private LoginListene loginListener;
    private UserInfoListener userInfoListener;

    TextView openidTextView;
    TextView nicknameTextView;
    Button loginButton;
    ImageView userlogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        initData();

    }

    private void init() {
        //用来登录的Button
        loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(this);
        //用来显示OpenID的textView
        openidTextView = (TextView) findViewById(R.id.user_openid);
        //用来显示昵称的textview
        nicknameTextView = (TextView) findViewById(R.id.user_nickname);
        //用来显示头像的Imageview
        userlogo = (ImageView) findViewById(R.id.user_logo);
    }

    private void initData() {
        mTencent = Tencent.createInstance(APPID, getApplicationContext());

        /*
        设置监听
         */
        loginListener = new LoginListene();
        userInfoListener = new UserInfoListener();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                LoginQQ();
                break;

        }

    }

    private void LoginQQ() {
        if (!mTencent.isSessionValid()) {
            mTencent.login(MainActivity.this, "all", loginListener);
        } else {
            if (mTencent != null) {
                mTencent.logout(MainActivity.this);
            }
        }

    }

    private class LoginListene implements IUiListener {

        @Override
        public void onComplete(Object value) {
            if (value == null) {
                return;
            }
            try {
                JSONObject jo = (JSONObject) value;
                System.out.println(jo);
                System.out.println("json=" + String.valueOf(jo));

                Toast.makeText(MainActivity.this, "成功",
                        Toast.LENGTH_LONG).show();


                String openID = jo.getString("openid");
                String accessToken = jo.getString("access_token");
                String expires = jo.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);


                if(mTencent.getQQToken() == null){
                    System.out.println("qqtoken == null");
                }
                mUserInfo = new UserInfo(MainActivity.this, mTencent.getQQToken());
                mUserInfo.getUserInfo(userInfoListener);

            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    }

    private class UserInfoListener implements IUiListener {

        @Override
        public void onComplete(Object values) {
            JSONObject jo = (JSONObject) values;
            System.out.println(jo.toString());
            String nickName = null;
            try {
                nickName = jo.getString("nickname");
                String mImageUrl = jo.getString("figureurl_qq_2");
                System.out.println(mImageUrl);
                String gender = jo.getString("gender");

                nicknameTextView.setText(nickName);
                Glide.with(MainActivity.this).load(mImageUrl).into(userlogo);



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.RESULT_LOGIN) {
                Tencent.handleResultData(data, loginListener);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
