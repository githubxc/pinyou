package com.xc.pinyou.event;

import com.xc.pinyou.MainActivity;
import com.xc.pinyou.ui.activity.ChatTestActivity;

import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;

/**
 * Created by xum19 on 2018/2/20.
 */

public class ImMessageHandler extends BmobIMMessageHandler {

    @Override
    public void onMessageReceive(MessageEvent messageEvent) {
        super.onMessageReceive(messageEvent);
        //在线消息

        //ChatTestActivity.tvView.setText("接收到："+messageEvent.getMessage().getContent()+"\n");


    }

    @Override
    public void onOfflineReceive(OfflineMessageEvent offlineMessageEvent) {
        super.onOfflineReceive(offlineMessageEvent);
        //离线消息
    }
}