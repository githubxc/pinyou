package com.xc.pinyou.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gyf.barlibrary.ImmersionBar;
import com.xc.pinyou.R;
import com.xc.pinyou.adapter.ConversationAdapter;
import com.xc.pinyou.adapter.OnRecyclerViewListener;
import com.xc.pinyou.adapter.base.base.IMutlipleItem;
import com.xc.pinyou.base.BaseFragment;
import com.xc.pinyou.bean.Conversation;
import com.xc.pinyou.bean.MyUser;
import com.xc.pinyou.bean.PrivateConversation;
import com.xc.pinyou.chat.MyChatEvent;
import com.xc.pinyou.chat.RefreshEvent;
import com.xc.pinyou.chat.model.UserModel;
import com.xc.pinyou.event.CarpoolEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.v3.BmobUser;

import static com.luck.picture.lib.tools.DebugUtil.log;

/**
 * Created by xum19 on 2018/2/7.
 */

public class MessageFragment extends BaseFragment {

    private static final String TAG = "MessageFragment";

    private ImmersionBar immersionBar;

    private RecyclerView rc_view;
    private SwipeRefreshLayout sw_refresh;

    ConversationAdapter adapter;
    LinearLayoutManager layoutManager;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTitle("消息");

        getConversations();

    }

    /**
     * 获取会话列表的数据
     * @return
     */
    private List<Conversation> getConversations(){
        //添加会话
        List<Conversation> conversationList = new ArrayList<>();
        conversationList.clear();
        //TODO 会话：4.2、查询全部会话
        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
        //Log.i(TAG, "getConversations: " + list.size());
        if(list!=null && list.size()>0){
            for (BmobIMConversation item:list){
                switch (item.getConversationType()){
                    case 1://私聊
                        conversationList.add(new PrivateConversation(item));
                        break;
                    default:
                        break;
                }
            }
        }
//
//        Log.i(TAG, "getConversations: " + conversationList.size());
//        Log.i(TAG, "getConversations: " + conversationList.get(0).getLastMessageContent());
//        Log.i(TAG, "getConversations: " + conversationList.get(0).getLastMessageTime());
//        Log.i(TAG, "getConversations: " + conversationList.toString());

        Collections.sort(conversationList);
        return conversationList;
    }

    @Override
    protected int getLayoutId() {
        immersionBar = ImmersionBar.with(this);
        immersionBar.statusBarColor(R.color.app_qing).fitsSystemWindows(true).init();
        return R.layout.fragment_message;
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
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (immersionBar != null)
            immersionBar.destroy();

    }

    @Override
    protected void findViews() {
        rc_view = getView(R.id.rc_view);
        sw_refresh = getView(R.id.sw_refresh);
    }

    @Override
    protected void formatViews() {

        //单一布局
        IMutlipleItem<Conversation> mutlipleItem = new IMutlipleItem<Conversation>() {

            @Override
            public int getItemViewType(int postion, Conversation c) {
                return 0;
            }

            @Override
            public int getItemLayoutId(int viewtype) {
                return R.layout.item_conversation;
            }

            @Override
            public int getItemCount(List<Conversation> list) {
                return list.size();
            }
        };
        adapter = new ConversationAdapter(getActivity(),mutlipleItem, null);
        rc_view.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getActivity());
        rc_view.setLayoutManager(layoutManager);
        sw_refresh.setEnabled(true);
        setListener();

    }

    @Override
    public void onResume() {
        super.onResume();
        sw_refresh.setRefreshing(true);
        query();
    }

    private void setListener(){

        sw_refresh.setRefreshing(true);
        query();

        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query();
            }
        });
        adapter.setOnRecyclerViewListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                adapter.getItem(position).onClick(getActivity());
                EventBus.getDefault().postSticky(new MyChatEvent("message", null,null, null));
            }

            @Override
            public boolean onItemLongClick(int position) {
                adapter.getItem(position).onLongClick(getActivity());
                adapter.remove(position);
                return true;
            }
        });
    }
    /**
     查询本地会话
     */
    public void query(){

        adapter.bindDatas(getConversations());
        adapter.notifyDataSetChanged();
        sw_refresh.setRefreshing(false);


//        if (getConversations().size() != 0){
//            adapter.bindDatas(getConversations());
//            adapter.notifyDataSetChanged();
//
//        }
//        sw_refresh.setRefreshing(false);
        //Log.i(TAG, "query: " + getConversations().get(0).getAvatar().toString());

        //Glide.with(context).load(getConversations().get(0).getAvatar().toString())
        //        .into((ImageView)getView(R.id.iv_recent_avatar));
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


    /**注册自定义消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshEvent event){
        log("---会话页接收到自定义消息---");
        //因为新增`新朋友`这种会话类型
        adapter.bindDatas(getConversations());
        adapter.notifyDataSetChanged();
    }

    /**注册消息接收事件
     * @param event
     * 1、与用户相关的由开发者自己维护，SDK内部只存储用户信息
     * 2、开发者获取到信息后，可调用SDK内部提供的方法更新会话
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event){
        //重新获取本地消息并刷新列表
        adapter.bindDatas(getConversations());
        adapter.notifyDataSetChanged();
    }

    /**注册离线消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event){
        //重新刷新列表
        adapter.bindDatas(getConversations());
        adapter.notifyDataSetChanged();
    }
}
