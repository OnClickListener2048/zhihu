package com.example.fx50j.zhihu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fx50j.zhihu.R;
import com.example.fx50j.zhihu.bean.DiscussContent;
import com.example.fx50j.zhihu.view.GlideCircleTransform;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ============================================================
 * 作 者 : XYJ
 * 版 本 ： 1.0
 * 创建日期 ： 2016/6/26 14:50
 * 描 述 ：评论信息Adapter
 * 修订历史 ：
 * ============================================================
 **/
public class DiscussContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DiscussContent> mListData;
    private Context mContext;
    private View view;
    private OnLikeClickListener onLikeClickListener;
    private boolean isShort = true;
    public DiscussContentAdapter(List<DiscussContent> mListData, Context context,boolean isShort) {
        this.mListData = mListData;
        this.mContext = context;
        this.isShort = isShort;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.recycler_discuss_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DiscussContent content = mListData.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.mTvName.setText(content.getAuthor());
        viewHolder.mTvContent.setText(content.getContent());
        viewHolder.mTvZanNum.setText(content.getLikes() + "");

        viewHolder.mTvTime.setText(MillFrmeTime(content.getTime()));
        /*
        Glide：图片加载库，
         */
        Glide.with(mContext).load(content.getAvatar())
                .transform(new GlideCircleTransform(mContext)).into(viewHolder.mIvIcon);
        viewHolder.mIvLike.setImageResource(mListData.get(position).isLiked()?R.mipmap.ic_vote_thumb_blue:R.mipmap.unliked);
        viewHolder.mIvLike.setOnClickListener((view) ->{
            viewHolder.mIvLike.setImageResource(mListData.get(position).isLiked()?R.mipmap.ic_vote_thumb_blue:R.mipmap.unliked);
            onLikeClickListener.onLikeClick(view,holder.getAdapterPosition(),isShort);
        });
    }

    public interface OnLikeClickListener {
        void onLikeClick(View view, int position, boolean isShort);
    }


    public void setOnLikeClickListener(OnLikeClickListener onLikeClickListener) {
        this.onLikeClickListener = onLikeClickListener;
    }

    private String MillFrmeTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");

        return format.format(date);
    }

    @Override
    public int getItemCount() {
        return mListData == null ? 0 : mListData.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mIvIcon;
        private ImageView mIvLike;
        private TextView mTvName;
        private TextView mTvZanNum;
        private TextView mTvContent;
        private TextView mTvTime;

        public ViewHolder(View itemView) {
            super(itemView);
            mIvIcon = (ImageView) view.findViewById(R.id.iv_icon);
            mTvName = (TextView) view.findViewById(R.id.tv_name);
            mTvZanNum = (TextView) view.findViewById(R.id.tv_zan_num);
            mTvContent = (TextView) view.findViewById(R.id.tv_discuss_content);
            mTvTime = (TextView) view.findViewById(R.id.tv_discuss_time);
            mIvLike = (ImageView) view.findViewById(R.id.iv_like);

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
}
