package com.example.recyclerviewtablayout;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

/**
 * tab与 RecyclerView 滑动联动
 */
public class SecondActivity extends Activity {
    private static final String TAG = "SecondActivity";

    private TabLayout mTabLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mManager;
    private TabRecyclerAdapter mAdapter;

    private String titles[] = new String[]{"标题一", "标题二", "标题三", "标题四", "标题五", "标题六", "标题七", "标题八", "标题九", "标题十"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mTabLayout = findViewById(R.id.tab_layout);
        mRecyclerView = findViewById(R.id.recycler_view);

        initTab();
        mAdapter = new TabRecyclerAdapter();
        mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @org.jetbrains.annotations.NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 通过 LinearLayoutManager 获得第一个可见的 item 的 position，并设置tab滚动到该 position
                mTabLayout.setScrollPosition(mManager.findFirstVisibleItemPosition(), 0, true);
            }
        });

        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorBlue));
        mTabLayout.setTabTextColors(Color.GRAY, ContextCompat.getColor(this, R.color.colorBlue));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //点击tab的时候，RecyclerView自动滑到该tab对应的item位置
                mManager.scrollToPositionWithOffset(tab.getPosition(), 0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /**
     * 添加 tab 数据
     */
    private void initTab() {
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        for (int i = 0; i < titles.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(titles[i]).setTag(i));
        }
    }

    public class TabRecyclerAdapter extends RecyclerView.Adapter {

        public static final int VIEW_TYPE_ITEM = 1;
        public static final int VIEW_TYPE_FOOTER = 2;
        private int parentHeight;
        private int itemHeight;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        parentHeight = mRecyclerView.getHeight();
                        itemHeight = view.getHeight();

                    }
                });
                return new ItemViewHolder(view);
            } else {
                //Footer是最后留白的位置，以便最后一个item能够出发tab的切换
                View view = new View(parent.getContext());
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentHeight - itemHeight));
                return new FooterViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position != titles.length) {
                ((ItemViewHolder) holder).setData(position);

                ((ItemViewHolder) holder).itemView.getTop();
            }
        }


        @Override
        public int getItemCount() {
            return titles.length + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == titles.length) {
                return VIEW_TYPE_FOOTER;
            } else {
                return VIEW_TYPE_ITEM;
            }
        }

        class FooterViewHolder extends RecyclerView.ViewHolder {

            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {

            private TextView mTitle;

            public ItemViewHolder(View itemView) {
                super(itemView);
                mTitle = itemView.findViewById(R.id.title);
            }

            public void setData(int position) {
                mTitle.setText(titles[position]);
            }
        }
    }
}
