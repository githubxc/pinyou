package com.xc.pinyou.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xc.pinyou.R;
import com.xc.pinyou.bean.MyUser;
import com.xc.pinyou.event.CarpoolEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by xum19 on 2018/2/20.
 */

public class ChatTestActivity extends AppCompatActivity implements MessageListHandler{

    private static final String TAG = "ChatTestActivity";

    @BindView(R.id.tv_id1)
    TextView tvId1;
    @BindView(R.id.tv_id2)
    TextView tvId2;
    @BindView(R.id.ll_chat)
    LinearLayout llChat;

    public static TextView tvView;
    @BindView(R.id.btn_send_message)
    Button btnSendMessage;
    @BindView(R.id.et_chat)
    EditText etChat;
    private MyUser user;
    private String id1;

    BmobIMConversation mBmobIMConversation;
    BmobIMUserInfo info =new BmobIMUserInfo();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chattest);
        ButterKnife.bind(this);

        user = BmobUser.getCurrentUser(MyUser.class);

        tvView = findViewById(R.id.tv_view);

        connectIm();

    }

    private void connectIm() {
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    Toast.makeText(ChatTestActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                    Log.i("TAG","服务器连接成功");

                }else {
                    Log.i("TAG",e.getMessage()+"  "+e.getErrorCode());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 注册订阅者
        if (!EventBus.getDefault().isRegistered(this))
        {
            Log.i(TAG, "onStart: " + "zhuce");
            EventBus.getDefault().register(this);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(CarpoolEvent event) {
        // 更新界面

        info.setAvatar(event.carpool.getCarpoolId().getUserAvatar().getFileUrl());
        info.setUserId(event.carpool.getCarpoolId().getObjectId());
        info.setName(event.carpool.getCarpoolId().getUsername());

        tvId1.setText(event.carpool.getCarpoolId().getObjectId());
        tvId2.setText(user.getObjectId());

    }


    @OnClick(R.id.btn_send_message)
    public void onViewClicked() {

        startConversation();
    }

    private void startConversation() {

        BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
            @Override
            public void done(BmobIMConversation c, BmobException e) {
                if(e==null){

                    //在此跳转到聊天页面或者直接转化
                    mBmobIMConversation=BmobIMConversation.obtain(BmobIMClient.getInstance(),c);
                    tvView.append("发送者：" + etChat.getText().toString()+"\n");
                    BmobIMTextMessage msg =new BmobIMTextMessage();
                    msg.setContent(etChat.getText().toString());
                    mBmobIMConversation.sendMessage(msg, new MessageSendListener() {
                        @Override
                        public void done(BmobIMMessage msg, BmobException e) {
                            if (e != null) {
                                etChat.setText("");
                            }else{
                            }
                        }
                    });
                }else{
                    Toast.makeText(ChatTestActivity.this, "开启会话出错", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BmobIM.getInstance().addMessageListHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注销订阅者
        EventBus.getDefault().unregister(this);
        BmobIM.getInstance().removeMessageListHandler(this);
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {

        tvView.setText(list.get(0).getMessage().getContent());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BmobIM.getInstance().disConnect();
    }
}
