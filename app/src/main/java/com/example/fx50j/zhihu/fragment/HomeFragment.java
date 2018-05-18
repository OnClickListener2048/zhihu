package com.example.fx50j.zhihu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.fx50j.zhihu.R;
import com.example.fx50j.zhihu.adapter.RecyclerAdapter;
import com.example.fx50j.zhihu.adapter.SpaceItemDecoration;
import com.example.fx50j.zhihu.bean.TopStories;
import com.example.fx50j.zhihu.ui.WebActivity;
import com.example.fx50j.zhihu.utils.CharsetStringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.recyclerview.adapter.SlideInRightAnimatorAdapter;

/**
 * ============================================================
 * 作 者 : XYJ
 * 版 本 ： 1.0
 * 创建日期 ： 2016/6/23 10:07
 * 描 述 ：首页
 * 修订历史 ：
 * ============================================================
 **/
public class HomeFragment extends Fragment {

    /*
    请求网络成功
     */
    public static final int REQUEST_NET_SUCCESS = 1;

    /*
    定时切换ViewPager Item
     */
    public static final int CHANGE_PAGER_ITEM = 0;

    /*
    获取知乎最新消息
     */
    public static final String mNewUrl = "http://news-at.zhihu.com/api/4/news/latest";
    /*
    消息内容获取
     */
    public static final String mMessageContentUrl = "http://news-at.zhihu.com/api/4/news/";

    /*
    加载更多Flag
     */
    private static final int FLAG_LOADING_MORE = 2;

    /*
    下拉刷新Flag
     */
    private static final int FLAG_PULL_REFRESH = 3;

    /*
    RecyclerView
     */
    private RecyclerView recyclerview;

    /*
    存放ViewPager加载的View
     */
    List<View> mViewList;
    /*
    存放从网络上获取的轮播图信息
     */
    List<TopStories> mTopStoriesList;

    /*
    存放今日热闻信息
     */
    List<TopStories> mStoriesList;

    /*
    Volley相关
     */
    private RequestQueue mQueue;
    private StringRequest mRequest;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerAdapter adapter;

    private LinearLayoutManager mLinearLayoutManager;

    /*
    日报日期，用于获取过往消息
     */
    private String mDate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        recyclerview = (RecyclerView) view.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        mViewList = new ArrayList<>();
        mTopStoriesList = new ArrayList<>();
        mStoriesList = new ArrayList<>();

        return view;
    }


    /*
    Handler
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case REQUEST_NET_SUCCESS:
                    refreshLayout.setRefreshing(false);
                    break;

            }

        }
    };


    /*
    Fragment生命周期，在此执行请求网络数据，并且更新界面数据
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*
        刚进入界面时刷新数据
         */
        requestNet(FLAG_PULL_REFRESH, mNewUrl);

        /*
        设置Item间距
         */
        recyclerview.addItemDecoration(new SpaceItemDecoration(20));

        /*
        自动轮播
         */
        handler.sendEmptyMessageDelayed(CHANGE_PAGER_ITEM, 5000);


        System.out.println("onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();

        System.out.println("onStart");

        /*
        给SwipeRefreshLayout设置监听事件 当刷新的时候请求网络
         */
        refreshLayout.setOnRefreshListener(() -> {
            requestNet(FLAG_PULL_REFRESH, mNewUrl);
        });

        /*
        RecyclerView滚动事件监听，
         */
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //后面需要拼接日期： 如 20131119
                String beforeUrl = "http://news.at.zhihu.com/api/4/news/before/";
                if (mLinearLayoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1) {
                        /*
                        加载更多数据
                         */
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        requestNet(FLAG_LOADING_MORE, beforeUrl + (Integer.parseInt(mDate)));
                    }
                }
            }
        });

    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {

        /*
        设置RecyclerAdapter
         */
        if (adapter == null) {
            adapter = new RecyclerAdapter(mStoriesList, getContext(), mViewList);
            SlideInRightAnimatorAdapter animatorAdapter = new SlideInRightAnimatorAdapter(adapter, recyclerview);
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            recyclerview.setHasFixedSize(true);
            recyclerview.setItemAnimator(new DefaultItemAnimator());
            recyclerview.setLayoutManager(mLinearLayoutManager);

            /*
        设置RecyclerView 的Item点击事件
         */
            adapter.setOnItemClickListener((view, position) -> {
            /*
            跳转至WebActivity，将内容显示在WebView中
             */
                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.putExtra("stories", mStoriesList.get(position));
                startActivity(intent);
            });
            recyclerview.setAdapter(animatorAdapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    /**
     * 请求网络
     */

    private void requestNet(int flag, String url) {
        mQueue = Volley.newRequestQueue(getContext());
        /*
        获取广告轮播图信息
         */
        mRequest = new CharsetStringRequest(StringRequest.Method.GET, url,
                json -> {
                    try {
                        JSONObject object = new JSONObject(json);

                        //获取日期
                        mDate = object.getString("date");


                        if (flag == FLAG_PULL_REFRESH) {
                            if (mViewList.size() > 0) {
                                mStoriesList.clear();
                                mViewList.clear();
                                mTopStoriesList.clear();
                            }

                            //Item == 0 时加载Viewpager
                            TopStories viewPager = new TopStories(null, -1, -1, null, -1, mDate);
                            mStoriesList.add(viewPager);
                        }

                        //随后加载日期
                        TopStories dateStories = new TopStories(null, -1, -1, null, 0, mDate);
                        mStoriesList.add(dateStories);

                        System.out.println("size:" + mStoriesList.size());

                        /*
                        获取今日热闻信息
                         */
                        JSONArray todayArray = object.getJSONArray("stories");
                        for (int i = 0; i < todayArray.length(); i++) {
                            JSONObject todayObject = todayArray.getJSONObject(i);
                            String image = todayObject.getJSONArray("images").getString(0);
                            int id = todayObject.getInt("id");
                            int ga_prefix = todayObject.getInt("ga_prefix");
                            String title = todayObject.getString("title");

                            TopStories stories = new TopStories(image, id, ga_prefix, title, 1, null);
                            mStoriesList.add(stories);

                        }

                        /*
                        只有下拉刷新时才获取广告轮播图信息
                         */
                        if (flag == FLAG_PULL_REFRESH) {
                            JSONArray adArray = object.getJSONArray("top_stories");
                            for (int i = 0; i < adArray.length(); i++) {
                                JSONObject topObject = adArray.getJSONObject(i);
                                String image = topObject.getString("image");
                                int id = topObject.getInt("id");
                                int ga_prefix = topObject.getInt("ga_prefix");
                                String title = topObject.getString("title");

                                TopStories stories = new TopStories(image, id, ga_prefix, title);
                                mTopStoriesList.add(stories);

                                mViewList.add(initPagerView(title, adArray, image));
                            }
                        }

                        /*
                         初始化RecyclerView
                         */
                        initRecyclerView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, volleyError -> {
            Snackbar.make(recyclerview, "网络请求出现问题", Snackbar.LENGTH_SHORT).show();

        });

        /*
        将Volley的请求添加的队列中
         */
        mQueue.add(mRequest);

        /*
        请求成功
         */
        handler.sendEmptyMessage(REQUEST_NET_SUCCESS);

    }


    /*
    ViewPager要加载的View
     */
    public View initPagerView(String title, JSONArray array, String imageUrl) {
        /*
        布局文件 填充成ViewPager要加载的View对象
         */
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.pager_item, null);

        ImageView imagpager = (ImageView) view.findViewById(R.id.iv_pager);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        content.setText(title);

        /*
        Glide:第三方框架，图片加载库
         */
        Glide.with(this).load(imageUrl).into(imagpager);

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        System.out.println("onDestroy");

        /*
        关闭Volley
         */
        mQueue.stop();
        mRequest.cancel();


    }
}
