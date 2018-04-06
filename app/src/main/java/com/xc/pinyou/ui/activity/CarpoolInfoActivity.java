package com.xc.pinyou.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mylhyl.circledialog.CircleDialog;
import com.xc.pinyou.MainActivity;
import com.xc.pinyou.R;
import com.xc.pinyou.base.BaseActivity;
import com.xc.pinyou.bean.Carpool;
import com.xc.pinyou.bean.MyUser;
import com.xc.pinyou.bean.Success;
import com.xc.pinyou.chat.ChatActivity;
import com.xc.pinyou.chat.MyChatEvent;
import com.xc.pinyou.event.CarpoolEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by xum19 on 2018/2/19.
 */

public class CarpoolInfoActivity extends BaseActivity {

    private static final String TAG = "CarpoolInfoActivity";

    private TextView tvInfoUserName, tvInfoCredibility, tvInfoStartPoint, tvInfoEndPoint, tvInfoStartTime, tvInfoPeople;
    private ImageView ivSemmage, ivCarpool, ivUserAvater;

    private String picUrl, userId, userName;
    private Carpool carpoolId;

    private int people;

    private boolean canCarpool = false;

    MyUser user = BmobUser.getCurrentUser(MyUser.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("拼车信息");
        setBaseLeftIcon(R.mipmap.back, "后退", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CarpoolInfoActivity.this, MainActivity.class));
                activity.finish();

            }
        });

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
    protected int getLayoutId() {
        return R.layout.activity_carpoolinfo;
    }

    @Override
    protected void findViews() {
        tvInfoUserName = getView(R.id.carpool_info_user_name);
        tvInfoCredibility = getView(R.id.carpool_info_user_credibility);
        tvInfoStartPoint = getView(R.id.info_tv_start_point);
        tvInfoEndPoint = getView(R.id.info_tv_end_point);
        tvInfoStartTime = getView(R.id.info_tv_start_time);
        tvInfoPeople = getView(R.id.info_tv_people);

        ivSemmage = getView(R.id.iv_message);
        ivCarpool= getView(R.id.iv_carpool);
        ivUserAvater = getView(R.id.carpool_info_user_avater);

        ivSemmage.setOnClickListener(this);
        ivCarpool.setOnClickListener(this);
    }

    @Override
    protected void formatViews() {

    }

    @Override
    protected void formatData() {

    }

    @Override
    protected void getBundle(Bundle bundle) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.iv_message:
                Log.i(TAG, "done: info :" + userId + "\n" + userName);
                EventBus.getDefault().postSticky(new MyChatEvent("carpool", userId, picUrl, userName));
                BmobIMUserInfo info = new BmobIMUserInfo(userId, userName, picUrl);
                Log.i(TAG, "done: info :" + info.getAvatar() + "\n" + info.getName()
                        + "\n" + info.getUserId());
                //TODO 会话：4.1、创建一个常态会话入口，好友聊天
                BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);
                Bundle bundle = new Bundle();
                bundle.putSerializable("c", conversationEntrance);
                startActivity(ChatActivity.class, bundle);
                break;
            case R.id.iv_carpool:
                new CircleDialog.Builder(this)
                        .setCanceledOnTouchOutside(true)
                        .setCancelable(true)
                        .setText("确定拼车吗？")
                        .setNegative("取消", null)
                        .setPositive("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (canCarpool){
                                    goCarpool();
                                }else {
                                    toast("不能和自己拼车！");
                                }
                            }
                        })
                        .show();
                break;
        }

    }

    private void goCarpool(){
        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        Success success = new Success(carpoolId, myUser, false);
        success.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                  toast("拼车成功");
                }else {
                    Log.i(TAG, "done: " + e.getErrorCode() + " " + e.getMessage());
                }
            }
        });
    }


    /**启动指定Activity
     * @param target
     * @param bundle
     */
    public void startActivity(Class<? extends Activity> target, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(activity, target);
        if (bundle != null)
            intent.putExtra(activity.getPackageName(), bundle);
        activity.startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(CarpoolEvent event) {
        // 更新界面

        if ((event.getTagId()).equals("我的出行") || (event.getTagId()).equals("我的发出")){
            ivSemmage.setVisibility(View.GONE);
            ivCarpool.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(event.carpool.getCarpoolId().getUserAvatar().getFileUrl())
                .apply(new RequestOptions().circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(ivUserAvater);

        picUrl = event.carpool.getCarpoolId().getUserAvatar().getFileUrl();
        userId = event.carpool.getCarpoolId().getObjectId();
        userName = event.carpool.getCarpoolId().getUsername();

        if ((user.getObjectId()).equals(userId)){
            canCarpool = false;
        }else {
            canCarpool = true;
        }

        Log.i(TAG, "onMessageEvent: " + picUrl + "\n" +
        userId + "\n" + userName);

        Log.i(TAG, "getCredibility: " + event.getCarpool().getCredibility().getCredibility());
        tvInfoUserName.setText(event.carpool.getCarpoolId().getUsername());
        tvInfoCredibility.setText(event.getCarpool().getCredibility().getCredibility() + "");
        tvInfoStartPoint.setText(event.carpool.getStartPoint());
        tvInfoEndPoint.setText(event.carpool.getEndPoint());
        tvInfoStartTime.setText(event.carpool.getStartTime().getDate());
        tvInfoPeople.setText(event.carpool.getPeople() + "");

        carpoolId = event.carpool;
        people = event.carpool.getPeople();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注销订阅者
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
