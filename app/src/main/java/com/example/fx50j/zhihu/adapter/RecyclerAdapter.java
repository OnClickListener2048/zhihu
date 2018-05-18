package com.example.fx50j.zhihu.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fx50j.zhihu.R;
import com.example.fx50j.zhihu.bean.TopStories;
import com.example.fx50j.zhihu.utils.DateUtil;

import java.util.List;

/**
 * ============================================================
 * 作 者 : XYJ
 * 版 本 ： 1.0
 * 创建日期 ： 2016/6/26 14:50
 * 描 述 ：主页日报列表Adapter
 * 修订历史 ：
 * ============================================================
 **/
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<TopStories> mListData;
    /*
    ViewPager显示的View
     */
    private List<View> mViewList;

    /*
    小圆点所在的父布局
     */
    private LinearLayout mRootLayout;
    private MyPagerAdapter adapter;

    private Context mContext;
    /*
    日报列表布局 所填充的View对象
     */
    private View view;

    /*
    只有第一次才发消息切换ViewPager
     */
    private boolean mIsChangePager = false;

    public static final int ITEM_NEWS_DATE = 0;//日报日期
    public static final int ITEM_NEWS = 1;//日报Item
    public static final int ITEM_VIEW_PAGER = -1;//ViewPager Item
    private ViewPager mViewPager;

    public RecyclerAdapter(List<TopStories> mListData, Context context, List<View> viewlist) {
        this.mListData = mListData;
        this.mContext = context;
        this.mViewList = viewlist;
    }

    public RecyclerAdapter(List<TopStories> mListData, Context context) {
        this.mListData = mListData;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (mListData.get(position).getType() == 0) {
            return ITEM_NEWS_DATE;
        } else if (mListData.get(position).getType() == 1) {
            return ITEM_NEWS;
        } else if (mListData.get(position).getType() == -1) {
            return ITEM_VIEW_PAGER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_NEWS) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.recycler_item, parent, false);
            return new ViewHolder(view);
        } else if (viewType == ITEM_NEWS_DATE) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.recycler_date_item, parent, false);
            return new DateViewHolder(view);
        } else if (viewType == ITEM_VIEW_PAGER) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.recycler_view_pager, null);
            return new ViewPagerHolder(view);
        }


        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.mTextView.setText(mListData.get(position).getTitle());
        /*
        Glide：图片加载库，
         */
            if (mListData.get(position).getImage() != null) {
                Glide.with(mContext).load(mListData.get(position).getImage()).into(viewHolder.mImageView);
            } else {
                viewHolder.mImageView.setVisibility(View.GONE);
            }
        } else if (holder instanceof DateViewHolder) {
            DateViewHolder viewHolder = (DateViewHolder) holder;
            viewHolder.mTvDate.setText(DateUtil.formatDate(mListData.get(position).getDate()));
        } else if (holder instanceof ViewPagerHolder) {
            //ViewPagerHolder viewHolder = (ViewPagerHolder) holder;
            setViewPager();
        }

    }

    private void setViewPager() {
        adapter = new MyPagerAdapter();
        mViewPager.setAdapter(adapter);

        /*
        只设置一次ViewPager监听，和每隔五秒发消息轮播
         */
        if (!mIsChangePager) {
            mViewPager.addOnPageChangeListener(new MyPagerListener());

            mIsChangePager = true;
            handler.sendEmptyMessageDelayed(0, 5000);
        }
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    /*
    Handler自动轮播ViewPager
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentItem = mViewPager.getCurrentItem();
            mViewPager.setCurrentItem(++currentItem);

            handler.sendEmptyMessageDelayed(0, 5000);
        }
    };

    /*
    日报列表
     */
    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageView;
        private TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.iv_icon);
            mTextView = (TextView) itemView.findViewById(R.id.tv_detail);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getPosition());
            }
        }

    }

    //RecyclerView 没有提供点击事件，这里是定义接口回调
    public onItemClickListener itemClickListener;

    public void setOnItemClickListener(onItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }

    /*
    日期Adapter
     */
    private class DateViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvDate;

        public DateViewHolder(View itemView) {
            super(itemView);
            mTvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }

    /*
    ViewPager Holder
     */
    private class ViewPagerHolder extends RecyclerView.ViewHolder {


        public ViewPagerHolder(View itemView) {
            super(itemView);
            mViewPager = (ViewPager) itemView.findViewById(R.id.viewpager);
            mRootLayout = (LinearLayout) itemView.findViewById(R.id.root_layout);
        }

    }

    /*
   ViewPager切换监听事件
    */
    private class MyPagerListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /*
                切换小圆点
                 */

            int childCount = mRootLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                mRootLayout.getChildAt(i).setEnabled(position % 5 == i);
            }

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }


    /*
    ViewPager Adapter
     */
    class MyPagerAdapter extends PagerAdapter {

        /*
        ViewPager的个数，这里设置成int的最大值，可以无限轮播
         */
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            /*
            官方推荐写法
             */
            return view == object;
        }

        /*
        添加ViewPager要显示的View
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position % mViewList.size()));
            return mViewList.get(position % mViewList.size());
        }

        /*
        删除ViewPager上一次显示的View，节省内存
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

// super.destroyItem(container, position, object);
container.removeView(mViewList.get(position % mViewList.size()));
        }
    }


}
