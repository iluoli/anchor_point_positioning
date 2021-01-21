package com.example.recyclerviewtablayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

/**
 * 标题tab滑动悬停到顶部
 * <p>
 * tab与被 NestedScrollView 包裹的 RecyclerView 滑动联动
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private TabLayout mTabLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mManager;
    private TabRecyclerAdapter mAdapter;
    private NestedScrollView mScrollView;
    private LinearLayout llFixTitle, llScrollTitle;
    private TextView mText;

    private String titles[] = new String[]{"标题一", "标题二", "标题三", "标题四", "标题五", "标题六", "标题七", "标题八", "标题九", "标题十"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScrollView = findViewById(R.id.scrollView);
        mText = findViewById(R.id.tv_text);
        mTabLayout = findViewById(R.id.tab_layout);
        mRecyclerView = findViewById(R.id.recycler_view);
        llFixTitle = findViewById(R.id.ll_fix_title);
        llScrollTitle = findViewById(R.id.ll_scroll_title);

        initTab();
        mAdapter = new TabRecyclerAdapter();
        mManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAdapter);

        // 设置 tab 点击监听
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mScrollView.smoothScrollTo(0, mRecyclerView.getChildAt(tab.getPosition()).getTop() + mRecyclerView.getTop());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // 设置 Scrollview 滚动监听
        mScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            // tab 顶部悬停判断
            if (scrollY >= llScrollTitle.getTop()) {
                // 判断 mTabLayout 每个 parent 只加载一次
                if (mTabLayout.getParent() != llFixTitle) {
                    llScrollTitle.removeAllViews();
                    llFixTitle.addView(mTabLayout);
                }
            } else {
                if (mTabLayout.getParent() != llScrollTitle) {
                    llFixTitle.removeAllViews();
                    llScrollTitle.addView(mTabLayout);
                }
            }

            // 添加 tab 联动
            for (int i = titles.length - 1; i >= 0; i--) {
                if (scrollY >= getScrollHeight(i)) {
                    // 跳转到指定的 tab
                    mTabLayout.setScrollPosition(i, 0, true);
                    break;
                }
            }
        });
    }

    /**
     * 获取 RecyclerView 每个 item 的滑动高度
     *
     * @param position position
     * @return scroll height
     */
    private int getScrollHeight(int position) {
        return mRecyclerView.getTop() + mRecyclerView.getChildAt(position).getTop();
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
                //Footer是最后留白的位置，以便最后一个item能够出发tab的切换（与 Scrollview 嵌套不起作用，需要自己在页面添加留白）
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

    public void onClick(View view) {
        startActivity(new Intent(MainActivity.this, SecondActivity.class));
    }
}
