package com.xc.pinyou.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
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
import com.xc.pinyou.view.CircleImageView;

import java.util.List;

/**
 * Created by xum19 on 2018/2/16.
 */

public class SearchCarpoolAdapter extends BaseQuickAdapter<Carpool, BaseViewHolder> {

    private Context mContext;

    public SearchCarpoolAdapter(int layoutResId, @Nullable List<Carpool> data, Context context) {
        super(layoutResId, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Carpool item) {
        CircleImageView iv = helper.getView(R.id.carpool_info_user_avater);
        Glide.with(mContext)
                .load(item.getCarpoolId().getUserAvatar().getFileUrl())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(iv);
        helper.setText(R.id.info_tv_start_time, item.getStartTime().getDate());
        helper.setText(R.id.carpool_info_user_name, item.getCarpoolId().getUsername());
        helper.setText(R.id.carpool_info_user_credibility, String.valueOf(item.getCredibility().getCredibility()));
        helper.setText(R.id.info_tv_start_point, item.getStartPoint());
        helper.setText(R.id.info_tv_end_point, item.getEndPoint());
        ((TextView)helper.getView(R.id.info_tv_people)).setText(item.getPeople() + "");
        ((ImageView)helper.getView(R.id.iv_message)).setVisibility(View.GONE);
        ((ImageView)helper.getView(R.id.iv_carpool)).setVisibility(View.GONE);
    }
}
