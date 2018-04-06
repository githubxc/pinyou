package com.xc.pinyou.ui.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.barlibrary.ImmersionBar;
import com.maiml.library.BaseItemLayout;
import com.maiml.library.config.ConfigAttrs;
import com.maiml.library.config.Mode;
import com.xc.pinyou.R;
import com.xc.pinyou.base.BaseFragment;
import com.xc.pinyou.bean.Credibility;
import com.xc.pinyou.bean.MyUser;
import com.xc.pinyou.bean.Success;
import com.xc.pinyou.event.Event;
import com.xc.pinyou.ui.activity.LoginActivity;
import com.xc.pinyou.ui.activity.MyCarpoolActivity;
import com.xc.pinyou.ui.activity.MyCommendActivity;
import com.xc.pinyou.ui.activity.MyInfoActivity;
import com.xc.pinyou.utils.DisplayUtils;
import com.xc.pinyou.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.FindListener;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xum19 on 2018/2/7.
 */

public class MyFragment extends BaseFragment {

    private static final String TAG = "MyFragment";

    private ImmersionBar immersionBar;

    private TextView tvUserName;
    private RatingBar ratingBar;
    private ImageView userAvater;

    private BaseItemLayout baseItemLayout;
    private Button btnExitLogin;

    MyUser myUser = BmobUser.getCurrentUser(MyUser.class);

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    protected int getLayoutId() {

        immersionBar = ImmersionBar.with(this);
        immersionBar.init();
        return R.layout.fragment_my;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            immersionBar = ImmersionBar.with(this);
            immersionBar.init();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        immersionBar.destroy();
    }

    @Override
    protected void findViews() {
        tvUserName = getView(R.id.tv_user_name);
        ratingBar = getView(R.id.ratingbar);
        userAvater = getView(R.id.ci_avater);

        baseItemLayout = getView(R.id.base_item_layout);

        btnExitLogin = getView(R.id.btn_exit_login);
        
        btnExitLogin.setOnClickListener(this);
    }

    @Override
    protected void formatViews() {

        List<String> valueList = new ArrayList<>();
        valueList.add("我的发出");
        valueList.add("我的出行");
        valueList.add("我的评价");
        valueList.add("我的设置");

        List<Integer> resIdList = new ArrayList<>();

        resIdList.add(R.mipmap.fachu);
        resIdList.add(R.mipmap.chuxing);
        resIdList.add(R.mipmap.pingjia);
        resIdList.add(R.mipmap.setting);

        ConfigAttrs attrs = new ConfigAttrs(); // 把全部参数的配置，委托给ConfigAttrs类处理。

        //参数 使用链式方式配置
        attrs.setValueList(valueList)  // 文字 list
                .setResIdList(resIdList) // icon list
                .setIconWidth(24)  //设置icon 的大小
                .setIconHeight(24)
                .setItemMarginTop(10)
                .setItemMode(Mode.ARROW)
                .setArrowResId(R.mipmap.forword3);
        baseItemLayout.setConfigAttrs(attrs)
                .create();

        baseItemLayout.setOnBaseItemClick(new BaseItemLayout.OnBaseItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position){
                    case 0:
                        EventBus.getDefault().postSticky(new Event("我的发出"));
                        jumpTo(MyCarpoolActivity.class);
                        break;
                    case 1:
                        EventBus.getDefault().postSticky(new Event("我的出行"));
                        jumpTo(MyCarpoolActivity.class);
                        break;
                    case 2:
                        jumpTo(MyCommendActivity.class);
                        break;
                    case 3:
                        jumpTo(MyInfoActivity.class);
                        break;
                }
            }
        });

    }

    @Override
    protected void formatData() {

        Log.i(TAG, "formatData: " + myUser.getUsername());
        BmobQuery<MyUser> query = new BmobQuery();
        query.addWhereEqualTo("objectId", myUser.getObjectId());
        query.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null){

                    tvUserName.setText(list.get(0).getUsername());

                    Glide.with(context)
                            .load(list.get(0).getUserAvatar().getFileUrl())
                            .apply(new RequestOptions().circleCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE))
                            .into(userAvater);

                }else
                    Log.i(TAG, "queryUserFail: " + e.getMessage() + " " + e.getErrorCode());
            }
        });

        BmobQuery<Credibility> credibilityBmobQuery = new BmobQuery<>();
        credibilityBmobQuery.addWhereEqualTo("user", myUser);
        credibilityBmobQuery.findObjects(new FindListener<Credibility>() {
            @Override
            public void done(List<Credibility> list, BmobException e) {
                if (e == null){
                    for (Credibility c:list){
                        if ((c.getUser().getObjectId()).equals(myUser.getObjectId()))
                            ratingBar.setRating(list.get(0).getCredibility());
                    }

                }else {
                    Log.i(TAG, "获取信誉度失败: " + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }

    @Override
    protected void getBundle() {

    }

    @Override
    public void onClick(View view) {
        
        switch (view.getId()){
            case R.id.btn_exit_login://退出登录
                exitLogin();
                break;
            }
        }

    @Override
    public void onResume() {
        super.onResume();
        BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.i(TAG, "done: " + "同步成功");
                    MyUser user = BmobUser.getCurrentUser(MyUser.class);
                    tvUserName.setText(user.getUsername());
                    Glide.with(context)
                            .load(user.getUserAvatar().getFileUrl())
                            .apply(new RequestOptions().circleCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE))
                            .into(userAvater);
                } else {
                    Log.i(TAG, "done: " + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }

    private void exitLogin() {

        BmobUser.logOut();
        BmobUser currentUser = BmobUser.getCurrentUser(MyUser.class); // 现在的currentUser是null了
        if (currentUser == null) {

            jumpTo(LoginActivity.class);
            activity.finish();
        }
    }


}
