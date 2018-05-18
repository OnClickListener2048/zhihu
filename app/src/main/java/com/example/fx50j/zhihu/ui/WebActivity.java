package com.example.fx50j.zhihu.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.example.fx50j.zhihu.R;
import com.example.fx50j.zhihu.adapter.DiscussContentAdapter;
import com.example.fx50j.zhihu.adapter.SpaceItemDecoration;
import com.example.fx50j.zhihu.bean.Discuss;
import com.example.fx50j.zhihu.bean.DiscussContent;
import com.example.fx50j.zhihu.bean.NewsContent;
import com.example.fx50j.zhihu.bean.TopStories;
import com.example.fx50j.zhihu.db.Dao;
import com.example.fx50j.zhihu.fragment.HomeFragment;
import com.example.fx50j.zhihu.utils.CharsetStringRequest;
import com.example.fx50j.zhihu.utils.DateUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class WebActivity extends SwipeBackActivity implements FloatingActionButton.OnClickListener, DiscussContentAdapter.onItemClickListener, DiscussContentAdapter.OnLikeClickListener {

    private WebView mWebView;
    private Toolbar mToolbar;
    private ImageView mImgContent;

    /*
    新闻额外信息Url，输入新闻的ID，获取对应新闻的额外信息，如评论数量，所获的『赞』的数量。
     */
    private static final String mExtraInfoUrl = "http://news-at.zhihu.com/api/4/story-extra/";

    /*
    收藏
     */
    private FloatingActionButton mFabFavorite;

    /*
    评论信息
     */
    private FloatingActionButton mFabDiscuss;

    /*
    ToolBar折叠效果的布局
     */
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    /*
    图片和内容的网页，从HomeFragment传递过来的
     */
    private String mContentUrl;
    private int mNewsId;

    /*
    存放长/短评论信息的集合
     */
    private List<DiscussContent> mDiscussList;
    private List<DiscussContent> mShortDiscussList;
    private List<DiscussContent> mLongDiscussList;
    private boolean isOnShort = true;
    /*
    Volley相关
     */
    private CharsetStringRequest mRequest;
    private RequestQueue mQueue;
    private DiscussContentAdapter adapter;
    private ProgressBar mProgressBar;
    private NewsContent content;
    private TopStories mTopStories;
    private String mShortDiscussUrl;
    private String mLongDiscussUrl;
    private BottomSheetDialog mDialog;
    private TextView mTvShortDiscuss;
    private TextView mTvLongDiscuss;
    private TextView mTvZanNum;
    private RecyclerView mRecyclerView;
    private Discuss mShortDiscussObj;
    private Discuss mLongDiscussObj;
    private EditText mEtReply;
    private Button mBtnReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        mTopStories = (TopStories) intent.getSerializableExtra("stories");
        mNewsId = mTopStories.getId();
        mContentUrl = HomeFragment.mMessageContentUrl + mNewsId;

        init();

    }


    private void init() {
        /*
        WebView
         */
        mWebView = (WebView) findViewById(R.id.web_view);
        WebSettings mWebSettings = mWebView.getSettings();
        //设置可以支持缩放
        mWebSettings.setSupportZoom(true);

        /*
        ToolBar
         */
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        ImageView
         */
        mImgContent = (ImageView) findViewById(R.id.iv_content);

        /*
        FloatingActionButton
         */
        mFabDiscuss = (FloatingActionButton) findViewById(R.id.fab_discuss);
        mFabFavorite = (FloatingActionButton) findViewById(R.id.fab_favorite);
        mFabFavorite.setOnClickListener(this);
        mFabDiscuss.setOnClickListener(this);

        mShortDiscussUrl = "http://news-at.zhihu.com/api/4/story/" + mNewsId + "/short-comments";
        mLongDiscussUrl = "http://news-at.zhihu.com/api/4/story/" + mNewsId + "/long-comments";
        /*
        请求内容数据
         */
        requestNet();
        getDiscussNums();
        initBottomSheetDialog();
        mProgressBar.setVisibility(View.GONE);
    }

    private void requestLongDiscussContent() {
        String longDiscuss = SPUtils.getInstance().getString(mLongDiscussUrl);
        if (longDiscuss == null || TextUtils.equals(longDiscuss, "")) {
            getLongDiscussContent();
            LogUtils.d("从网络请求的长评论数据。。。。。");
        } else {
            LogUtils.d("从本地请求的长评论数据。。。。。"+longDiscuss);
            Gson gson = new Gson();
            mLongDiscussObj = gson.fromJson(longDiscuss, Discuss.class);
            mLongDiscussList = mLongDiscussObj.getComments();
            updateNum(mTvLongDiscuss,mLongDiscussList,"长");
        }
    }

    private void requestShortDiscussContent() {
        String shortDiscuss = SPUtils.getInstance().getString(mShortDiscussUrl);

        if (shortDiscuss == null || TextUtils.equals(shortDiscuss, "")) {
            getShortDiscussContent();
            LogUtils.d("从网络请求的短评论数据。。。。。");
        } else {
            LogUtils.d("从本地请求的短评论数据。。。。。"+shortDiscuss);
            Gson gson = new Gson();
            mShortDiscussObj = gson.fromJson(shortDiscuss, Discuss.class);
            mShortDiscussList = mShortDiscussObj.getComments();
            updateNum(mTvShortDiscuss,mShortDiscussList,"短");
            updateLikesNum(mShortDiscussList);
            /*
        默认显示短评论
         */
            adapter = new DiscussContentAdapter(mShortDiscussList, this,true);
            adapter.setOnLikeClickListener(this);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(adapter);
        }

    }

    private void updateNum(TextView textView,List<DiscussContent> list,String prefix) {
        int num = 0;
        if (list!=null) {
            num = list.size();
        }

        textView.setText(prefix+"评论"+"("+String.valueOf(num)+")");
    }

    private void updateLikesNum(List<DiscussContent> list) {
        int num = 0;
        for (DiscussContent discussContent : list) {
            num += discussContent.getLikes();
        }

        mTvZanNum.setText(String.valueOf(num));
    }


    void getDiscussNums() {
          /*
        获取评论与点赞数量
         */
        mRequest = new CharsetStringRequest(mExtraInfoUrl + mNewsId, json -> {
            LogUtils.d(json);
            try {
                JSONObject extraObject = new JSONObject(json);
//                mTvLongDiscuss.setText("长评论(" + extraObject.getString("long_comments") + ")");
//                mTvShortDiscuss.setText("短评论(" + extraObject.getString("short_comments") + ")");
//                mTvZanNum.setText(extraObject.getString("popularity"));
                if (TextUtils.equals(extraObject.getString("long_comments"), "0")&&SPUtils.getInstance().getString(mLongDiscussUrl)==null) {
                    mTvLongDiscuss.setText("长评论(" + extraObject.getString("long_comments") + ")");
                } else {
                    requestLongDiscussContent();
                }

                if (TextUtils.equals(extraObject.getString("short_comments"), "0")&&SPUtils.getInstance().getString(mShortDiscussUrl)==null) {
                    mTvLongDiscuss.setText("长评论(" + extraObject.getString("long_comments") + ")");
                } else {
                    requestShortDiscussContent();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, volleyError -> {
        });

        mQueue.add(mRequest);

    }

    private void getLongDiscussContent() {

        if (mLongDiscussList == null) {
            mLongDiscussList = new ArrayList<>();
        }

        if (mLongDiscussList.size() > 0) {
            mLongDiscussList.clear();
        }

        mQueue = Volley.newRequestQueue(this);
        mRequest = new CharsetStringRequest(mLongDiscussUrl, json -> {
            try {
                LogUtils.d(json);
                Gson gson = new Gson();
                mLongDiscussObj = gson.fromJson(json, Discuss.class);
                SPUtils.getInstance().put(mLongDiscussUrl, json);
                mProgressBar.setVisibility(View.GONE);
                mLongDiscussList = mLongDiscussObj.getComments();
                updateNum(mTvLongDiscuss,mLongDiscussList,"长");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, volleyError -> {
        });

        mQueue.add(mRequest);
    }

    private void getShortDiscussContent() {
        if (mShortDiscussList == null) {
            mShortDiscussList = new ArrayList<>();
        }

        if (mShortDiscussList.size() > 0) {
            mShortDiscussList.clear();
        }

        mQueue = Volley.newRequestQueue(this);
        mRequest = new CharsetStringRequest(mShortDiscussUrl, json -> {
            try {
                LogUtils.d(json);
                Gson gson = new Gson();
                mShortDiscussObj = gson.fromJson(json, Discuss.class);
                SPUtils.getInstance().put(mShortDiscussUrl, json);
                mShortDiscussList = mShortDiscussObj.getComments();
                updateNum(mTvShortDiscuss,mShortDiscussList,"短");
                updateLikesNum(mShortDiscussList);
                    /*
        默认显示短评论
         */
                adapter = new DiscussContentAdapter(mShortDiscussList, this,true);
                adapter.setOnLikeClickListener(this);
                LinearLayoutManager manager = new LinearLayoutManager(this);
                mRecyclerView.setLayoutManager(manager);
                mRecyclerView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, volleyError -> {
        });

        mQueue.add(mRequest);
    }

    private void requestNet() {
        mQueue = Volley.newRequestQueue(this);
        //mTvBody.setText(Html.fromHtml(content.getBody(),new URLImageParser(mTvBody),null));
        mRequest = new CharsetStringRequest(CharsetStringRequest.Method.GET,
                mContentUrl, json -> {

            Gson gson = new Gson();
            content = gson.fromJson(json, NewsContent.class);

            //mTvBody.setText(Html.fromHtml(content.getBody(),new URLImageParser(mTvBody),null));

            handleContent(content);

        }, volleyError -> {
        });

        mQueue.add(mRequest);
    }

    /*
    利用WebView加载数据
     */
    private void handleContent(NewsContent newsContent) {
        Glide.with(this).load(newsContent.getImage()).into(mImgContent);
        mCollapsingToolbarLayout.setTitle(newsContent.getTitle());
        String mNewsContent = "<link rel=\"stylesheet\" type=\"text/css\" href=\"news_content_style.css\"/>"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"news_header_style.css\"/>"
                + newsContent.getBody().replace("<div class=\"img-place-holder\">", "");
        mWebView.loadDataWithBaseURL("file:///android_asset/", mNewsContent, "text/html", "UTF-8", null);
        mWebView.setDrawingCacheEnabled(true);
    }

    /*
    FloatingActionButton 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_favorite://收藏
                Dao dao = new Dao(this);
                if (dao.queryOnly(mNewsId)) {
                    dao.delete(mNewsId);
                    Snackbar.make(v, "取消收藏", Snackbar.LENGTH_SHORT).show();
                } else {
                    boolean result = dao.insert(mTopStories.getId(), mTopStories.getImage(), mTopStories.getTitle());
                    Snackbar.make(v, result ? "收藏成功" : "收藏失败", Snackbar.LENGTH_SHORT).show();
                }

                break;
            case R.id.fab_discuss://评论
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                } else {
                    mDialog.show();
                }

                break;
                default:
        }
    }

    /**
     * 显示评论底部对话框
     */
    private void initBottomSheetDialog() {
        mDialog = new BottomSheetDialog(this);
        View view = View.inflate(this, R.layout.dialog_bottom_sheet, null);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        /*
        短评论数量
         */
        mTvShortDiscuss = (TextView) view.findViewById(R.id.tv_short_discuss);
        /*
        长评论数量
         */
        mTvLongDiscuss = (TextView) view.findViewById(R.id.tv_long_discuss);
        /*
        点赞数
         */
        mTvZanNum = (TextView) view.findViewById(R.id.tv_zan_num);
        /*
        显示评论列表
         */
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(30));

        mEtReply = (EditText) view.findViewById(R.id.et_feedback);
        mBtnReply = (Button) view.findViewById(R.id.feedback_send);

        mBtnReply.setOnClickListener(view2 ->{
            attemptReply();

        });

        /*
        评论列表网址
         */

        /*
        给TextView添加点击事件
         */
        mTvLongDiscuss.setOnClickListener(v -> {
            mTvLongDiscuss.setTextColor(getResources().getColor(R.color.colorPrimary));
            mTvShortDiscuss.setTextColor(Color.BLACK);
            adapter = new DiscussContentAdapter(mLongDiscussList, this,false);
            adapter.setOnLikeClickListener(this);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(adapter);
            isOnShort = false;
        });

        mTvShortDiscuss.setOnClickListener(v -> {
            mTvShortDiscuss.setTextColor(getResources().getColor(R.color.colorPrimary));
            mTvLongDiscuss.setTextColor(Color.BLACK);
            adapter = new DiscussContentAdapter(mShortDiscussList, this,true);
            adapter.setOnLikeClickListener(this);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(adapter);
            isOnShort = true;
        });


        mDialog.setContentView(view);
    }

    private void attemptReply() {
        String reply = mEtReply.getText().toString();

        if (!SPUtils.getInstance().getBoolean("isQQLogin",false)) {
            ToastUtils.showShort("登录后才能回复");
            return;
        }

        if (TextUtils.equals("", reply)) {
            ToastUtils.showShort("回复内容不能为空");
        } else {
            SharedPreferences msp = getSharedPreferences("QQInfo", MODE_PRIVATE);
            String img = msp.getString("img", "");
            String nickname = msp.getString("nickname", "");
            DiscussContent discussContent = new DiscussContent(
                    nickname,
                    reply,
                    0,
                    TimeUtils.getNowMills(),
                    img,
                    false
            );
            Gson gson = new Gson();
            if (isOnShort) {
                if (mShortDiscussList==null) {
                    mShortDiscussList = new ArrayList<>();
                    adapter = new DiscussContentAdapter(mShortDiscussList, this,true);
                    mRecyclerView.setAdapter(adapter);
                }
                mShortDiscussList.add(discussContent);
                SPUtils.getInstance().put(mShortDiscussUrl,gson.toJson(mShortDiscussObj));
            } else {
                if (mLongDiscussList==null) {
                    mLongDiscussList = new ArrayList<>();
                    adapter = new DiscussContentAdapter(mLongDiscussList, this,false);
                    mRecyclerView.setAdapter(adapter);
                }
                mLongDiscussList.add(discussContent);
                SPUtils.getInstance().put(mLongDiscussUrl,gson.toJson(mLongDiscussObj));
            }
            adapter.notifyDataSetChanged();
            ToastUtils.showShort("回复成功");
            clearInput();
        }
        KeyboardUtils.hideSoftInput(WebActivity.this);
        mRecyclerView.requestFocus();
        updateReplyCount();
    }

    private void updateReplyCount() {
        if (isOnShort) {
            updateNum(mTvShortDiscuss, mShortDiscussList, "短");
        } else {
            updateNum(mTvLongDiscuss,mLongDiscussList,"长");
        }
    }

    private void clearInput() {
        mEtReply.setText("");
    }

//    /**
//     * 显示评论列表
//     *
//     * @param recyclerView
//     * @param url
//     */
//    private void showDiscuss(RecyclerView recyclerView, String url) {
//        if (mDiscussList == null) {
//            mDiscussList = new ArrayList<>();
//        }
//
//        if (mDiscussList.size() > 0) {
//            mDiscussList.clear();
//        }
//
//        mQueue = Volley.newRequestQueue(this);
//        mRequest = new CharsetStringRequest(url, json -> {
//            try {
//                LogUtils.d(json);
//
//                Gson gson = new Gson();
//                Discuss discuss = gson.fromJson(json, Discuss.class);
//                SPUtils.getInstance().put(url, json);
//                mDiscussList = discuss.getComments();
//
//
//                mProgressBar.setVisibility(View.GONE);
//
//                adapter = new DiscussContentAdapter(mDiscussList, this);
//                adapter.setOnItemClickListener(this);
//                LinearLayoutManager manager = new LinearLayoutManager(this);
//                recyclerView.setLayoutManager(manager);
//                recyclerView.setAdapter(adapter);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }, volleyError -> {
//        });
//
//        mQueue.add(mRequest);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_menu, menu);
        return true;
    }

    /*
        返回菜单监听
         */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            /*
            返回
             */
            case android.R.id.home:
                this.finish();
                break;
            case R.id.menu_share:
                Intent share_localIntent = new Intent(Intent.ACTION_SEND);
                share_localIntent.setType("text/plain");
                share_localIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                share_localIntent.putExtra(Intent.EXTRA_TEXT,
                        "来自《焦点日报》的分享:" + content.getTitle() + content.getShare_url());
                this.startActivity(Intent.createChooser(share_localIntent, "日报分享"));

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*
        关闭Volley
         */
        mQueue.stop();
        mRequest.cancel();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onLikeClick(View view, int position, boolean isShort) {
        Gson gson = new Gson();
        if (isShort) {
            DiscussContent discussContent = mShortDiscussList.get(position);
            discussContent.setLiked(!discussContent.isLiked());
            discussContent.setLikes(discussContent.isLiked() ? discussContent.getLikes() + 1 : discussContent.getLikes() - 1);
            SPUtils.getInstance().put(mShortDiscussUrl,gson.toJson(mShortDiscussObj));
            updateLikesNum(mShortDiscussList);

        } else {
            DiscussContent discussContent = mLongDiscussList.get(position);
            discussContent.setLiked(!discussContent.isLiked());
            discussContent.setLikes(discussContent.isLiked() ? discussContent.getLikes() + 1 : discussContent.getLikes() - 1);
            SPUtils.getInstance().put(mLongDiscussUrl,gson.toJson(mLongDiscussObj));
            mTvZanNum.setText(discussContent.isLiked()
                    ?String.valueOf(Integer.valueOf(mTvZanNum.getText().toString())+1)
                    :String.valueOf(Integer.valueOf(mTvZanNum.getText().toString())-1));
        }
        adapter.notifyDataSetChanged();

    }
}


