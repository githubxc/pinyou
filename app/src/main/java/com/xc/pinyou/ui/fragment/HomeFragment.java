package com.xc.pinyou.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajguan.library.EasyRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.xc.pinyou.R;
import com.xc.pinyou.adapter.HomeCarpoolAdapter;
import com.xc.pinyou.base.BaseFragment;
import com.xc.pinyou.bean.Carpool;
import com.xc.pinyou.bean.MyUser;
import com.xc.pinyou.event.CarpoolEvent;
import com.xc.pinyou.ui.activity.CarpoolInfoActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by xum19 on 2018/2/7.
 */

public class HomeFragment extends BaseFragment {

    private static final String TAG = "HomeFragment";
    private ImmersionBar immersionBar;

    //private EasyRefreshLayout easyRefreshLayout;
    private SwipeRefreshLayout easyRefreshLayout;
    private RecyclerView recyclerView;
    private HomeCarpoolAdapter adapter;

    private List<Carpool> carpools = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle("首页");

        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        Log.i(TAG, "onViewCreated: " + myUser.getUsername());
    }


    @Override
    protected int getLayoutId() {
        Log.i(TAG, "getLayoutId: ");
        //immersionBar = ImmersionBar.with(this);
        //immersionBar.statusBarColor(R.color.app_qing).fitsSystemWindows(true).init();
        return R.layout.fragment_home;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            immersionBar = ImmersionBar.with(this);
            immersionBar.statusBarColor(R.color.app_qing).fitsSystemWindows(true).init();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (immersionBar != null)
            immersionBar.destroy();
    }

    @Override
    protected void findViews() {
      recyclerView = getView(R.id.recyclerList);
     easyRefreshLayout = getView(R.id.easylayout);
    }

    @Override
    protected void formatViews() {
        Log.i(TAG, "formatViews: ");
    }

    @Override
    protected void formatData() {
        query();
        adapter = new HomeCarpoolAdapter(R.layout.home_recycleview_item, carpools, context);
    }

    private void initRecycleViewDate() {

        //创建布局管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager); //创建适配器

        //添加自定义分割线
//        DividerItemDecoration divider = new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
//        divider.setDrawable(ContextCompat.getDrawable(context,R.drawable.drvider_shape));
//        recyclerView.addItemDecoration(divider);
        adapter.openLoadAnimation();
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                // 发布事件
                EventBus.getDefault().postSticky(new CarpoolEvent(carpools.get(position), "home"));
                Log.i(TAG, "onItemClick: " + carpools.get(position).toString() );
                Intent intent = new Intent(activity, CarpoolInfoActivity.class);
                activity.startActivity(intent);

            }
        });

        easyRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        query();
                        adapter.notifyDataSetChanged();
                        easyRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
//        easyRefreshLayout.addEasyEvent(new EasyRefreshLayout.EasyEvent() {
//            @Override
//            public void onLoadMore() {
////              easyRefreshLayout.loadMoreComplete(new EasyRefreshLayout.Event() {
////                  @Override
////                  public void complete() {
////                      adapter.notifyDataSetChanged();
////                  }
////              });
//               // easyRefreshLayout.loadMoreComplete();
//            }
//
//            @Override
//            public void onRefreshing() {
//
//                easyRefreshLayout.refreshComplete();
//            }
//        });
    }

    @Override
    protected void getBundle() {
        Log.i(TAG, "getBundle: ");
    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, "onClick: ");
    }

    private void query(){
        carpools.clear();
        BmobQuery<Carpool> query = new BmobQuery<>();
        query.include("carpoolId,credibility");
        query.findObjects(new FindListener<Carpool>() {
            @Override
            public void done(List<Carpool> list, BmobException e) {
                if (e == null){
                    for (Carpool carpool:list){
                        carpools.add(carpool);
                        Log.i(TAG, "done: " + carpool.getObjectId() + " " +
                                carpool.getCarpoolId().getObjectId() + " " +
                                carpool.getCarpoolId().getUsername());
                    }

                    initRecycleViewDate();

                }else {
                    Log.i(TAG, "done: " + e.getMessage() + " " + e.getErrorCode());
                }
            }
        });
    }
}
