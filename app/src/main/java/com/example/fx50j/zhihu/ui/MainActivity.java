package com.example.fx50j.zhihu.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.example.fx50j.zhihu.R;
import com.example.fx50j.zhihu.fragment.BeginGameFragment;
import com.example.fx50j.zhihu.fragment.BigCompanyFragment;
import com.example.fx50j.zhihu.fragment.CartoonFragment;
import com.example.fx50j.zhihu.fragment.DesignFragment;
import com.example.fx50j.zhihu.fragment.FinancialFragment;
import com.example.fx50j.zhihu.fragment.HomeFragment;
import com.example.fx50j.zhihu.fragment.InternetFragment;
import com.example.fx50j.zhihu.fragment.MovieFragment;
import com.example.fx50j.zhihu.fragment.MusicFragment;
import com.example.fx50j.zhihu.fragment.NoBoredFragment;
import com.example.fx50j.zhihu.fragment.RiChangFragment;
import com.example.fx50j.zhihu.fragment.SportsFragment;
import com.example.fx50j.zhihu.fragment.UserRecommendFragment;
import com.example.fx50j.zhihu.view.GlideCircleTransform;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private Toolbar toolbar;

    /*
    NavigationView相关
     */
    private ImageView mIvTouXiang;//头像
    private TextView mTvUserName;//用户名
    private View view;

    private SharedPreferences msp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = initToolBar("首页");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        view = View.inflate(this, R.layout.nav_header_main, null);

        navigationView.addHeaderView(view);

        /*
        隐藏NavigationView的滚动条
         */
        if (navigationView != null) {
            navigationView.getChildAt(0).setVerticalScrollBarEnabled(false);
        }

        mIvTouXiang = (ImageView) findViewById(R.id.iv_tou_xiang);
        mTvUserName = (TextView) findViewById(R.id.tv_user_name);


        /*
        默认加载首页的Fragment
         */

        initFragment(new HomeFragment(), "首页");

    }

    private boolean mIsShowSnackbar;

    @Override
    protected void onStart() {
        super.onStart();
        mIvTouXiang = (ImageView) view.findViewById(R.id.iv_tou_xiang);
        mTvUserName = (TextView) view.findViewById(R.id.tv_user_name);
        /*
        设置DrawLayout上的头像与昵称
         */
        msp = getSharedPreferences("QQInfo", MODE_PRIVATE);
        String img = msp.getString("img", "");
        String nickname = msp.getString("nickname", "");
        System.out.println(img + "--" + nickname);
        if (!TextUtils.isEmpty(img)) {
            if (!mIsShowSnackbar) {
                mIsShowSnackbar = true;
                Snackbar.make(getWindow().getDecorView(), "欢迎回来：" + nickname, Snackbar.LENGTH_SHORT).show();
                mTvUserName.setText(nickname);
                Glide.with(this).load(img).transform(new GlideCircleTransform(this)).into(mIvTouXiang);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        System.out.println("resultCode:" + resultCode);
//        if (resultCode == RESULT_OK) {
//            imgurl = data.getStringExtra("imgurl");
//            nickname = data.getStringExtra("nickname");
//            System.out.println("imgurl:" + imgurl);
//            if (nickname != null) {
//                Snackbar.make(getWindow().getDecorView(), nickname + "：欢迎回来！", Snackbar.LENGTH_SHORT).show();
//                //Glide.with(MainActivity.this).load(imgurl).into(mIvTouXiang);
//                //mTvUserName.setText(nickname);
//            }
//        }
    }

    //退出app
    private long mExitTime = 0;

    /*
        返回键关闭侧滑栏
         */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (System.currentTimeMillis() - mExitTime > 1000) {
                Snackbar.make(drawer, "双击返回键退出", Snackbar.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                this.finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_favorites) {
            startActivity(new Intent(this,MyFavoriteActivity.class));
            return true;
        }
        if (id == R.id.action_about_soft) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(getResources().getDrawable(R.mipmap.ic_launcher));
            builder.setTitle("关于软件");
            builder.setMessage("一款第三方日报，内容来自知乎日报 API 分析,版权属于知乎，如有不妥请告知");
            builder.create().show();

            return true;
        }
        if (id == R.id.action_login) {
            startActivityForResult(new Intent(this, LoginActivity.class), 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_shouye) {//首页

            initFragment(new HomeFragment(), "首页");

        } else if (id == R.id.nav_richang) {//日常心理学

            initFragment(new RiChangFragment(), "日常心理学");

        } else if (id == R.id.nav_yonghu) {//用户推荐日报

            initFragment(new UserRecommendFragment(), "用户推荐日报");

        } else if (id == R.id.nav_vedio) {//电影日报

            initFragment(new MovieFragment(), "电影日报");

        } else if (id == R.id.nav_bored) {//不许无聊

            initFragment(new NoBoredFragment(), "不许无聊");

        } else if (id == R.id.nav_design) {//设计日报

            initFragment(new DesignFragment(), "设计日报");

        } else if (id == R.id.nav_company) {//大公司日报

            initFragment(new BigCompanyFragment(), "大公司日报");

        } else if (id == R.id.nav_caijing) {//财经日报

            initFragment(new FinancialFragment(), "财经日报");

        } else if (id == R.id.nav_internet) {//互联网安全日报

            initFragment(new InternetFragment(), "互联网安全日报");

        } else if (id == R.id.nav_begin_game) {//开始游戏

            initFragment(new BeginGameFragment(), "开始游戏");

        } else if (id == R.id.nav_music) {//音乐日报

            initFragment(new MusicFragment(), "音乐日报");

        } else if (id == R.id.nav_cartoon) {//卡通日报

            initFragment(new CartoonFragment(), "卡通日报");

        } else if (id == R.id.nav_sports) {//体育日报

            initFragment(new SportsFragment(), "体育日报");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 加载Fragment
     *
     * @param fragment
     */
    public void initFragment(Fragment fragment, String title) {
        /*
        改变ToolBar Title
         */
        toolbar.setTitle(title);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtils.showLong("ssssssssssssssssssssssss");
    }
}
