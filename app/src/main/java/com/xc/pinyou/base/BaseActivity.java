package com.xc.pinyou.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.xc.pinyou.MainActivity;
import com.xc.pinyou.R;
import com.xc.pinyou.weight.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public final int BACK_ID = R.id.base_left_icon;
    public final int RIGHT_TEXT_ID = R.id.base_right_text;
    public final int RIGHT_ICON_ID1 = R.id.base_right_icon1;
    public final int RIGHT_ICON_ID2 = R.id.base_right_icon2;

    /**
     * 用于传递的上下文信息
     */
    public Context context;
    public Activity activity;

    private ImageView baseLeftIcon;
    private ViewStub titleStub;

    private RelativeLayout relativeLayout;

    /**
     * 当前打开Activity存储List
     */
    private static List<Activity> activities = new ArrayList<>();

    /**
     * 加载提示框
     */
    private CustomProgressDialog customProgressDialog;

    Unbinder unbind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activities.add(this);
        context = getApplicationContext();
        activity = this;

        setContentView(R.layout.activity_base);

        //不传文本
        customProgressDialog = new CustomProgressDialog(activity, R.style.progress_dialog_loading);
        //传递文本
        customProgressDialog = new CustomProgressDialog(activity, R.style.progress_dialog_loading, "玩命加载中。。。");

        initSDK();
        this.setBaseContentView(this.getLayoutId());

        ButterKnife.bind(activity);
        findViews();
        getBundle(getIntent().getBundleExtra("bundle"));
        formatViews();
        formatData();
    }

    public void setTitleBackground(int color){
        if (titleStub == null) {
            titleStub = getView(R.id.base_title_layout);
            titleStub.inflate();
        }

        relativeLayout = getView(R.id.base_bg);

        relativeLayout.setBackgroundResource(color);
    }

    /**
     * 隐藏头布局
     */
    public void hideTitle() {
        getView(R.id.base_title_layout).setVisibility(View.GONE);
    }

    /**
     * 隐藏返回键
     */
    public void hideBack() {
        baseLeftIcon.setVisibility(View.GONE);
    }

    /**
     * 显示返回键
     */
    public void showBack() {
        baseLeftIcon.setVisibility(View.VISIBLE);
    }

    /**
     * 设置标题
     *
     * @param title 标题的文本
     */
    public void setTitle(String title) {
        setTitle(title, true);
    }

    /**
     * 设置标题
     *
     * @param titleId 标题的文本
     */
    public void setTitle(int titleId) {
        setTitle(titleId, true);
    }

    /**
     * 设置标题
     *
     * @param title    标题的文本
     * @param showBack 是否显示返回键
     */
    public void setTitle(String title, boolean showBack) {
        if (titleStub == null) {
            titleStub = getView(R.id.base_title_layout);
            titleStub.inflate();
        }
        ((TextView) getView(R.id.base_title)).setText(title);
        baseLeftIcon = getView(R.id.base_left_icon);
        baseLeftIcon.setVisibility(showBack ? View.VISIBLE : View.GONE);
        setBaseBack(null);
    }

    /**
     * 设置标题
     *
     * @param titleId  标题的文本
     * @param showBack 是否显示返回键
     */
    public void setTitle(int titleId, boolean showBack) {
        if (titleStub == null) {
            titleStub = getView(R.id.base_title_layout);
            titleStub.inflate();
        }
        ((TextView) getView(R.id.base_title)).setText(titleId);
        baseLeftIcon = getView(R.id.base_left_icon);
        baseLeftIcon.setVisibility(showBack ? View.VISIBLE : View.GONE);
        setBaseBack(null);
    }

    /**
     * 设置返回点击事件
     *
     * @param clickListener 点击事件监听者
     */
    public void setBaseBack(View.OnClickListener clickListener) {
        if (baseLeftIcon == null) {
            titleStub = getView(R.id.base_title_layout);
            titleStub.inflate();
            baseLeftIcon = getView(R.id.base_left_icon);
        }
        if (clickListener == null) {
            baseLeftIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            baseLeftIcon.setOnClickListener(clickListener);
        }
    }

    /**
     * 最右侧图片功能
     * @param resId             图片ID
     * @param alertText
     * @param clickListener     点击事件
     * @return                  返回imageview
     */
    public ImageView setBaseLeftIcon(int resId, String alertText, View.OnClickListener clickListener) {
        if (titleStub == null) {
            titleStub = getView(R.id.base_title_layout);
            titleStub.inflate();
        }

        ImageView baseLeftIcon = getView(R.id.base_left_icon);
        baseLeftIcon.setImageResource(resId);
        baseLeftIcon.setVisibility(View.VISIBLE);
        //语音辅助提示的时候读取的信息
        baseLeftIcon.setContentDescription(alertText);
        baseLeftIcon.setOnClickListener(clickListener);
        return baseLeftIcon;
    }

    /**
     * 最右侧图片功能键设置方法
     *
     * @param resId         图片id
     * @param alertText     语音辅助提示读取信息
     * @param clickListener 点击事件
     * @return 将当前ImageView返回方便进一步处理
     */
    public ImageView setBaseRightIcon1(int resId, String alertText, View.OnClickListener clickListener) {
        if (titleStub == null) {
            titleStub = getView(R.id.base_title_layout);
            titleStub.inflate();
        }

        ImageView baseRightIcon1 = getView(R.id.base_right_icon1);
        baseRightIcon1.setImageResource(resId);
        baseRightIcon1.setVisibility(View.VISIBLE);
        //语音辅助提示的时候读取的信息
        baseRightIcon1.setContentDescription(alertText);
        baseRightIcon1.setOnClickListener(clickListener);
        return baseRightIcon1;
    }

    /**
     * 右数第二个图片功能键设置方法
     *
     * @param resId         图片id
     * @param alertText     语音辅助提示读取信息
     * @param clickListener 点击事件
     * @return 将当前ImageView返回方便进一步处理
     */
    public ImageView setBaseRightIcon2(int resId, String alertText, View.OnClickListener clickListener) {
        if (titleStub == null) {
            titleStub = getView(R.id.base_title_layout);
            titleStub.inflate();
        }

        ImageView baseRightIcon2 = getView(R.id.base_right_icon2);
        baseRightIcon2.setImageResource(resId);
        baseRightIcon2.setVisibility(View.VISIBLE);
        //语音辅助提示的时候读取的信息
        baseRightIcon2.setContentDescription(alertText);
        baseRightIcon2.setOnClickListener(clickListener);
        return baseRightIcon2;
    }

    /**
     * 最右侧文本功能键设置方法
     *
     * @param text          文本信息
     * @param clickListener 点击事件
     * @return 将当前TextView返回方便进一步处理
     */
    public TextView setBaseRightText(String text, View.OnClickListener clickListener) {
        if (titleStub == null) {
            titleStub = getView(R.id.base_title_layout);
            titleStub.inflate();
        }

        TextView baseRightText = getView(R.id.base_right_text);
        baseRightText.setText(text);
        baseRightText.setVisibility(View.VISIBLE);
        baseRightText.setOnClickListener(clickListener);
        return baseRightText;
    }

    /**
     * 最右侧文本功能键设置方法
     *
     * @param textId        文本信息id
     * @param clickListener 点击事件
     * @return 将当前TextView返回方便进一步处理
     */
    public TextView setBaseRightText(int textId, View.OnClickListener clickListener) {
        if (titleStub == null) {
            titleStub = getView(R.id.base_title_layout);
            titleStub.inflate();
        }

        TextView baseRightText = getView(R.id.base_right_text);
        baseRightText.setText(textId);
        baseRightText.setVisibility(View.VISIBLE);
        baseRightText.setOnClickListener(clickListener);
        return baseRightText;
    }

    /**
     * 引用头部布局
     *
     * @param layoutId 布局id
     */
    private void setBaseContentView(int layoutId) {
        LinearLayout layout = getView(R.id.base_main_layout);

        //获取布局，并在BaseActivity基础上显示
        final View view = getLayoutInflater().inflate(layoutId, null);
        //关闭键盘
        hideKeyBoard();
        //给EditText的父控件设置焦点，防止键盘自动弹出
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layout.addView(view, params);
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyBoard() {
        View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void jumpTo(Class<?> clz) {
        startActivity(new Intent(this, clz));
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void jumpTo(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转页面
     *
     * @param clz         所跳转的Activity类
     * @param requestCode 请求码
     */
    public void jumpTo(Class<?> clz, int requestCode) {
        startActivityForResult(new Intent(this, clz), requestCode);
    }

    /**
     * 跳转页面
     *
     * @param clz         所跳转的Activity类
     * @param bundle      跳转所携带的信息
     * @param requestCode 请求码
     */
    public void jumpTo(Class<?> clz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtra("bundle", bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 消息提示框
     *
     * @param message 提示消息文本
     */
    public void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 消息提示框
     *
     * @param messageId 提示消息文本ID
     */
    public void toast(int messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 关闭所有Activity（除MainActivity以外）
     */
    public void finishActivity() {
        for (Activity activity : activities) {
            activity.finish();
        }
    }

    /**
     * 跳转到指定的Activity
     *
     * @param clz 指定的Activity对应的class
     */
    public void backTo(Class<?> clz) {
        if (clz.equals(MainActivity.class)) {
            finishActivity();
        } else {
            for (int i = activities.size() - 1; i >= 0; i--) {
                if (clz.equals(activities.get(i).getClass())) {
                    break;
                } else {
                    activities.get(i).finish();
                }
            }
        }
    }

    /**
     * 显示加载提示框
     */
    public void showLoadDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                customProgressDialog.show();
            }
        });
    }

    /**
     * 隐藏加载提示框
     */
    public void hideLoadDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (customProgressDialog != null && customProgressDialog.isShowing()) {
                    customProgressDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (unbind != null)
        {
            unbind.unbind();
        }
        activities.remove(this);

    }

    /**
     * 检测当的网络（Wlan、3G/2G）状态
     *
     * @return true 表示网络可用
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                toast("当前网络是连接的");
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * SDK初始化
     */
    protected void initSDK() {
    }

    ;

    /**
     * 简化获取View
     *
     * @param viewId View的ID
     * @param <T>    将View转化为对应泛型，简化强转的步骤
     * @return ID对应的View
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        return (T) findViewById(viewId);
    }

    /**
     * 简化获取View
     *
     * @param view   父view
     * @param viewId View的ID
     * @param <T>    将View转化为对应泛型，简化强转的步骤
     * @return ID对应的View
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(View view, int viewId) {
        return (T) view.findViewById(viewId);
    }

    /**
     * 设置点击事件
     *
     * @param layouts 点击控件Id
     */
    protected void setOnClickListener(int... layouts) {
        for (int layout : layouts) {
            getView(layout).setOnClickListener(this);
        }
    }

    /**
     * 获取布局ID
     *
     * @return 获取的布局ID
     */
    protected abstract int getLayoutId();

    /**
     * 获取所有View信息
     */
    protected abstract void findViews();

    /**
     * 初始化布局信息
     */
    protected abstract void formatViews();

    /**
     * 初始化数据信息
     */
    protected abstract void formatData();

    /**
     * 初始化Bundle
     */
    protected abstract void getBundle(Bundle bundle);
}