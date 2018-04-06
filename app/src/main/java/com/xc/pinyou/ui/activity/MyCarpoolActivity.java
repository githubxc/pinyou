package com.xc.pinyou.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.autonavi.amap.mapcore.maploader.AMapLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xc.pinyou.R;
import com.xc.pinyou.adapter.HomeCarpoolAdapter;
import com.xc.pinyou.adapter.MyCarpoolAdapter;
import com.xc.pinyou.base.BaseActivity;
import com.xc.pinyou.bean.Carpool;
import com.xc.pinyou.bean.MyUser;
import com.xc.pinyou.bean.Success;
import com.xc.pinyou.event.CarpoolEvent;
import com.xc.pinyou.event.Event;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by xum19 on 2018/3/6.
 */

public class MyCarpoolActivity extends BaseActivity {

    private static final String TAG = "MyCarpoolActivity";

    private RecyclerView recyclerView;

    private MyCarpoolAdapter adapter, mywayAdapter;

    private List<Carpool> carpools;
    private List<Carpool> carpoolList, c;

    private String title;

    private List<Success> successList;

    MyUser myUser = BmobUser.getCurrentUser(MyUser.class);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mycarpool;
    }

    @Override
    protected void findViews() {
        recyclerView = getView(R.id.rv_mycarpool);
    }

    @Override
    protected void formatViews() {
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
    }

    @Override
    protected void formatData() {

    }

    @Override
    protected void getBundle(Bundle bundle) {
    }

    @Override
    public void onClick(View view) {

    }

    private void query(){
        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        BmobQuery<Carpool> query = new BmobQuery<>();
        query.include("carpoolId,credibility");
        query.addWhereEqualTo("carpoolId", myUser.getObjectId());
        query.findObjects(new FindListener<Carpool>() {
            @Override
            public void done(List<Carpool> list, BmobException e) {
                if (e == null){
                    for (Carpool carpool:list){
                        Log.i(TAG, "done: " + list.size() + "\n" +
                           carpool.getCarpoolId().getUsername());
                        carpool.setCarpoolId(myUser);
                        Log.i(TAG, "我的发出: " + carpool.getCredibility().getCredibility());
                        carpools.add(carpool);
                    }
                    adapter = new MyCarpoolAdapter(R.layout.home_recycleview_item, carpools, context);
                    adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            EventBus.getDefault().postSticky(new CarpoolEvent(carpools.get(position), "我的发出"));
                            Intent intent = new Intent(activity, CarpoolInfoActivity.class);
                            activity.startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }else {
                    Log.i(TAG, "done: " + e.getMessage() + " " + e.getErrorCode());
                }
            }
        });
    }

    private void queryMyCarpool() {
        Log.i(TAG, "queryMyCarpool: " + myUser.getObjectId());
        carpoolList = new ArrayList<>();
        carpoolList.clear();
        BmobQuery<Success> query = new BmobQuery<>();
        query.include("carpoolerId,authorId");
        query.findObjects(new FindListener<Success>() {
            @Override
            public void done(List<Success> list, BmobException e) {
                if ( e == null){
                    for (Success success : list){
                        Log.i(TAG, "done: " + list.size());
                        Log.i(TAG, "getCarpoolerId: " + list.get(0).getCarpoolerId().getObjectId());
                        if ((success.getCarpoolerId().getObjectId()).equals(myUser.getObjectId())){
                            successList.add(success);
                            carpoolList.add(success.getAuthorId());
                            getCarpoolInfo(success.getAuthorId().getObjectId());
                        }

                    }
                }else {
                    Log.i(TAG, "出错: " + e.getMessage() + " " + e.getErrorCode());
                }
            }
        });

    }

    private void getCarpoolInfo(String id){
        Log.i(TAG, "getCarpoolInfo: " + id);

        c.clear();
        BmobQuery<Carpool> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", id);
        query.include("carpoolId,credibility");
        query.findObjects(new FindListener<Carpool>() {
            @Override
            public void done(List<Carpool> list, BmobException e) {
                if (e == null){
                    for (Carpool carpool:list){
                        Log.i(TAG, "done: " + carpool.getCarpoolId().getUsername());
                        c.add(carpool);
                    }
                    mywayAdapter = new MyCarpoolAdapter(R.layout.home_recycleview_item, c, context);
                    mywayAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                            EventBus.getDefault().postSticky(new CarpoolEvent(c.get(position), "我的出行"));
                            Intent intent = new Intent(activity, CarpoolInfoActivity.class);
                            activity.startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(mywayAdapter);
                }else {
                    Log.i(TAG, "done: " + e.getMessage() + " " + e.getErrorCode());
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBaseLeftIcon(R.mipmap.back, "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(Event event) {
        title = event.getId();
        Log.i(TAG, "onMessageEvent: " + title);
        if (title.equals("我的发出")){
            carpools = new ArrayList<>();

            carpools.clear();
            query();
        }else if (title.equals("我的出行")){
            successList = new ArrayList<>();
            c = new ArrayList<>();
            queryMyCarpool();
            Log.i(TAG, "C的长度: " + c.size());

        }else {

        }

        setTitle(title);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 注册订阅者
        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
