package com.example.fx50j.zhihu.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * ============================================================
 * 作 者 : XYJ
 * 版 本 ： 1.0
 * 创建日期 ： 2016/6/26 19:23
 * 描 述 ：RecyclerView Item之间的间距
 * 修订历史 ：
 * ============================================================
 **/
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if(parent.getChildPosition(view) != 0) {
            outRect.top = space;
        }

    }
}
