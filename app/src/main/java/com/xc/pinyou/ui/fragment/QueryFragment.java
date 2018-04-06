package com.xc.pinyou.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.xc.pinyou.R;
import com.xc.pinyou.adapter.HomeCarpoolAdapter;
import com.xc.pinyou.adapter.SearchCarpoolAdapter;
import com.xc.pinyou.base.BaseFragment;
import com.xc.pinyou.bean.Carpool;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by xum19 on 2018/2/7.
 */

public class QueryFragment extends BaseFragment{

    private static final String TAG = "QueryFragment";

     private ImmersionBar immersionBar;

     private EditText startEt, endEt;
     private Button btnSearch;
     private RecyclerView searchRecycle;

     private SearchCarpoolAdapter adapter;
     private List<Carpool> carpools;

     private RelativeLayout rl;

     private TextView tvResult;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("查询");
    }

    @Override
    protected int getLayoutId() {
        immersionBar = ImmersionBar.with(this);
        immersionBar.statusBarColor(R.color.app_qing).fitsSystemWindows(true).init();
        return R.layout.fragment_query;
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
        startEt = getView(R.id.et_now_location);
        endEt = getView(R.id.et_goto);

        btnSearch = getView(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query();
                rl.setVisibility(View.VISIBLE);
                if (carpools.size() == 0){
                    tvResult.setVisibility(View.VISIBLE);
                }else {
                    tvResult.setVisibility(View.GONE);
                }
            }
        });

        searchRecycle = getView(R.id.rv_search);

        rl = getView(R.id.rl_search_result);
        tvResult = getView(R.id.tv_result);
    }

    @Override
    protected void formatViews() {
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        searchRecycle.setLayoutManager(manager);

       carpools = new ArrayList<>();
    }

    @Override
    protected void formatData() {

    }

    @Override
    protected void getBundle() {

    }

    @Override
    public void onClick(View view) {

    }

    private void query(){
        BmobQuery<Carpool> query = new BmobQuery<>();
        query.include("carpoolId,credibility");
        String startPoint = startEt.getText().toString(), endPoint = endEt.getText().toString();
        carpools.clear();
        query.findObjects(new FindListener<Carpool>() {
            @Override
            public void done(List<Carpool> list, BmobException e) {
                if (e == null){
                    for (Carpool c : list){
                        if ((c.getStartPoint()).equals(startPoint) || (c.getEndPoint()).equals(endPoint)){
                            carpools.add(c);
                            tvResult.setVisibility(View.GONE);
                        }else {
                            tvResult.setVisibility(View.VISIBLE);
                        }
                        Log.i(TAG, "done: " + c.getStartPoint() + "\n"
                        + c.getEndPoint());
                    }

                    adapter = new SearchCarpoolAdapter(R.layout.activity_carpoolinfo, carpools, context);
                    searchRecycle.setAdapter(adapter);

                }else {
                    Log.i(TAG, "done: " + e.getMessage() + " " + e.getErrorCode());
                }
            }
        });
    }
}
