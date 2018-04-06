package com.xc.pinyou.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xc.pinyou.R;
import com.xc.pinyou.bean.Carpool;
import com.xc.pinyou.bean.MyUser;
import com.xc.pinyou.bean.Success;
import com.xc.pinyou.view.CircleImageView;

import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;

/**
 * Created by xum19 on 2018/2/16.
 */

public class MyCommendAdapter extends BaseQuickAdapter<Carpool, BaseViewHolder> {

    private static final String TAG = "MyCommendAdapter";

    private Context mContext;

    public MyCommendAdapter(int layoutResId, @Nullable List<Carpool> data, Context context) {
        super(layoutResId, data);
        mContext = context;

    }

    @Override
    protected void convert(BaseViewHolder helper, Carpool item) {

        CircleImageView circleImageView = helper.getView(R.id.commend_user_avater);
        Glide.with(mContext)
                .load(item.getCarpoolId().getUserAvatar().getFileUrl())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(circleImageView);
        helper.setText(R.id.tv_commend_start_time, item.getStartTime().getDate());
        helper.setText(R.id.commend_user_name, item.getCarpoolId().getUsername());
        helper.setText(R.id.commend_user_credibility, String.valueOf(item.getCredibility().getCredibility()));
        helper.setText(R.id.tv_commend_start_point, item.getStartPoint());
        helper.setText(R.id.tv_commend_end_point, item.getEndPoint());

    }
}
