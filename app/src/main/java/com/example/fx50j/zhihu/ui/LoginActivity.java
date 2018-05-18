package com.example.fx50j.zhihu.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.blankj.utilcode.util.SPUtils;
import com.example.fx50j.zhihu.R;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ============================================================
 * 作 者 : XYJ
 * 版 本 ： 1.0
 * 创建日期 ： 2016/6/30 14:12
 * 描 述 ：
 * 修订历史 ：
 * ============================================================
 **/
public class LoginActivity extends AppCompatActivity {

    private static final String APPID = "1106841537";

    private Tencent mTencent;
    private UserInfo mUserInfo;
    private LoginListene loginListener;
    private UserInfoListener userInfoListener;
    /*
    头像、昵称相关
     */
    private String mImageUrl;
    private String mNickName;

    private SharedPreferences msp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("用户登录");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    /*
    登出
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTencent != null) {
            mTencent.logout(this);
        }
    }

    private void init() {
        mTencent = Tencent.createInstance(APPID, getApplicationContext());
        /*
        设置监听
         */
        loginListener = new LoginListene();
        userInfoListener = new UserInfoListener();
    }

    /*
    QQ登录
     */
    public void QQLogin(View view) {
        if (!mTencent.isSessionValid()) {
            mTencent.login(LoginActivity.this, "all", loginListener);
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
                System.out.println("json=" + String.valueOf(jo));

                String openID = jo.getString("openid");
                String accessToken = jo.getString("access_token");
                String expires = jo.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);


                if (mTencent.getQQToken() == null) {
                    System.out.println("qqtoken == null");
                }
                mUserInfo = new UserInfo(LoginActivity.this, mTencent.getQQToken());
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
            try {
                //头像
                mImageUrl = jo.getString("figureurl_qq_2");
                //昵称
                mNickName = jo.getString("nickname");
                SPUtils.getInstance().put("isQQLogin",true);
//                /*
//                返回数据
//                 */
//                Intent intent = new Intent();
//                intent.putExtra("imgurl", mImageUrl);
//                intent.putExtra("nickname", mNickName);
//                setResult(RESULT_OK, intent);
                /*
                保存账号信息
                 */
                msp = getSharedPreferences("QQInfo",MODE_PRIVATE);
                msp.edit().putString("img",mImageUrl).putString("nickname",mNickName).commit();
                LoginActivity.this.finish();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
