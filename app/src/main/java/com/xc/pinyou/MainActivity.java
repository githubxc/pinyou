package com.xc.pinyou;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.gyf.barlibrary.ImmersionBar;
import com.hjm.bottomtabbar.BottomTabBar;
import com.xc.pinyou.bean.Credibility;
import com.xc.pinyou.bean.MyUser;
import com.xc.pinyou.ui.fragment.CarpoolFragment;
import com.xc.pinyou.ui.fragment.HomeFragment;
import com.xc.pinyou.ui.fragment.MessageFragment;
import com.xc.pinyou.ui.fragment.MyFragment;
import com.xc.pinyou.ui.fragment.QueryFragment;
import com.xc.pinyou.utils.Util;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    //导航栏
    private BottomTabBar mBottomTabBar;
    private ImmersionBar immersionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomTabBar = findViewById(R.id.bottom_tab_bar);

        initBottomTabBar();

        immersionBar = ImmersionBar.with(this);
        immersionBar.statusBarColor(R.color.app_qing).fitsSystemWindows(true).init();

        Util.fetchUserInfo();
        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);

        BmobIM.connect(myUser.getObjectId(), new ConnectListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    Log.i("TAG","服务器连接成功");

                    BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo(myUser.getObjectId(),
                            myUser.getUsername(),
                            myUser.getUserAvatar().getFileUrl()));

                }else {
                    Log.i("TAG",e.getMessage()+"  "+e.getErrorCode());
                }
            }
        });

    }

    private void initBottomTabBar(){
        mBottomTabBar.init(getSupportFragmentManager())
                .setImgSize(75, 75)
                .setFontSize(12)
                .setChangeColor(getResources().getColor(R.color.app_qing),getResources().getColor(R.color.unselect))
                .setTabPadding(4, 6, 0)
                .addTabItem("首页", R.mipmap.home, R.mipmap.home_normal, HomeFragment.class)
                .addTabItem("拼车", R.mipmap.carpool, R.mipmap.carpool_normal, CarpoolFragment.class)
                .addTabItem("查询", R.mipmap.query, R.mipmap.query_normal, QueryFragment.class)
                .addTabItem("消息", R.mipmap.message, R.mipmap.message_normal, MessageFragment.class)
                .addTabItem("我的", R.mipmap.my, R.mipmap.my_normal, MyFragment.class)
                .isShowDivider(true)
                .setOnTabChangeListener(new BottomTabBar.OnTabChangeListener() {
                    @Override
                    public void onTabChange(int position, String name, View view) {
                        Log.i("TGA", "位置：" + position + "      选项卡：" + name);
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (immersionBar != null)
            immersionBar.destroy();  //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
    }
}
