package com.example.fx50j.zhihu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.recyclerview.adapter.SlideInRightAnimatorAdapter;

/**
 * ============================================================
 * 作 者 : XYJ
 * 版 本 ： 1.0
 * 创建日期 ： 2016/6/23 10:07
 * 描 述 ：开始游戏
 * 修订历史 ：
 * ============================================================
 **/
public class BeginGameFragment extends Fragment {
    private String url = "http://news-at.zhihu.com/api/4/theme/2";
    List<TopStories> mStoriesList;
    private ImageView imageView;
    private TextView textView;
    private RecyclerView recyclerview;
    private RequestQueue mQueue;
    private StringRequest mRequest;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerAdapter adapter;
    /*
    请求网络成功
     */
    public static final int REQUEST_NET_SUCCESS = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other_tab, null);
        imageView = (ImageView) view.findViewById(R.id.rc_image);
        recyclerview = (RecyclerView) view.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mStoriesList = new ArrayList<>();
        textView = (TextView) view.findViewById(R.id.rc_tv);
        TextView mTvTab = (TextView) view.findViewById(R.id.tv);
        mTvTab.setText("开始游戏");
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageView.setFocusableInTouchMode(true);
        imageView.setFocusable(true);

        requestNet();
         /*
        设置Item间距
         */
        recyclerview.addItemDecoration(new SpaceItemDecoration(20));
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        /*
        设置RecyclerAdapter
         */
        adapter = new RecyclerAdapter(mStoriesList, getContext());
        SlideInRightAnimatorAdapter animatorAdapter = new SlideInRightAnimatorAdapter(adapter, recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(layoutManager);

        /*
        解决NestedSrollView和RecyclerView事件冲突问题
         */
        recyclerview.setNestedScrollingEnabled(false);

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
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("onStart");

        /*
        给SwipeRefreshLayout设置监听事件 当刷新的时候请求网络
         */
        refreshLayout.setOnRefreshListener(() -> {
            requestNet();
        });
    }

    /**
     * 请求网络
     */
    private void requestNet() {
        mQueue = Volley.newRequestQueue(getContext());
        mRequest = new CharsetStringRequest(StringRequest.Method.GET, url,
                json -> {
                    try {
                        JSONObject object = new JSONObject(json);
                        /*
                         *获取不许无聊新闻
                         */
                        JSONArray rcArray = object.getJSONArray("stories");
                        System.out.println("rcArray:" + rcArray.toString());
                        String background = object.getString("background");
                        Glide.with(getContext()).load(background).into(imageView);
                        String description = object.getString("description");
                        textView.setText(description);
                        for (int i = 0; i < rcArray.length(); i++) {
                            JSONObject rcObject = rcArray.getJSONObject(i);
                            int id = rcObject.getInt("id");
                            String title = rcObject.getString("title");
                            if (rcObject.length() == 3) {
                                TopStories stories = new TopStories(null, id, 0, title,1,null);
                                mStoriesList.add(stories);
                            } else if (rcObject.length() == 4) {
                                String image = rcObject.getJSONArray("images").getString(0);
                                Log.e("TAG", image);
                                TopStories stories = new TopStories(image, id, 0, title,1,null);
                                mStoriesList.add(stories);
                            }
                        }
                        initRecyclerView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }, volleyError -> {
            Snackbar.make(recyclerview, "网络请求出现问题", Snackbar.LENGTH_SHORT).show();
        });
        mQueue.add(mRequest);
     /*
        请求成功
         */
        handler.sendEmptyMessage(REQUEST_NET_SUCCESS);
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
