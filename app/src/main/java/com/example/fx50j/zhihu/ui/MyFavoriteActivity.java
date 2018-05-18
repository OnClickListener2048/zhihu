package com.example.fx50j.zhihu.ui;

import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.fx50j.zhihu.R;
import com.example.fx50j.zhihu.adapter.RecyclerAdapter;
import com.example.fx50j.zhihu.adapter.SpaceItemDecoration;
import com.example.fx50j.zhihu.bean.TopStories;
import com.example.fx50j.zhihu.db.Dao;

import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class MyFavoriteActivity extends SwipeBackActivity {

    private RecyclerView mRecyclerView;
    private RecyclerAdapter adapter;
    private List<TopStories> storiesList;

    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorite);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        setTitle("我的收藏");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Dao dao = new Dao(this);
        storiesList = dao.query();
        LinearLayoutManager manager =  new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        adapter = new RecyclerAdapter(storiesList,this);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(20));
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((view, position) -> {
            Intent intent = new Intent(this,WebActivity.class);
            intent.putExtra("stories", storiesList.get(position));
            mPosition = position;
            startActivity(intent);

        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        getContentResolver().registerContentObserver(Uri.parse("content://delete"),
                true, new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);
                        if (adapter != null) {
                            storiesList.remove(mPosition);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
