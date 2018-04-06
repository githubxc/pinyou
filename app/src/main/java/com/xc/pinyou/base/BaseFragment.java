package com.xc.pinyou.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.gyf.barlibrary.ImmersionBar;
import com.xc.pinyou.R;
import com.xc.pinyou.weight.ProgressDialogFragment;

/**
 * Created by xum19 on 2017/11/16.
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    public final int BACK_ID = R.id.base_left_icon;
    public final int RIGHT_TEXT_ID = R.id.base_right_text;
    public final int RIGHT_ICON_ID1 = R.id.base_right_icon1;
    public final int RIGHT_ICON_ID2 = R.id.base_right_icon2;

    /**
     * 用于传递的上下文信息
     */
    protected Context context;
    protected Activity activity;
    private ImageView baseLeftIcon;

    private RelativeLayout relativeLayout;

    private View currentLayout;
    private ViewStub viewStub;
    private boolean isUseBase = false;

    /**
     * 加载提示框
     */
    ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance();

    /**
     * 隐藏头布局
     */
    protected void hideTitle() {
        viewStub.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the title_layout for this fragment
        currentLayout = inflater.inflate(R.layout.fragment_base, container, false);


        isUseBase = true;
        return currentLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isUseBase) {
            setBaseContentView(getLayoutId());
        }

        getBundle();
        findViews();
        formatData();
        formatViews();

    }

    /**
     * 显示加载提示框
     */
    public void showLoadDialog() {

        dialogFragment.show(getFragmentManager(), "tag");
    }

    /**
     * 隐藏加载提示框
     */
    public void hideLoadDialog() {
        dialogFragment.dismiss();
    }

    public void setTitleBackground(int color){
        if (viewStub == null) {
            viewStub = getView(R.id.base_title_layout);
            viewStub.inflate();
        }

        relativeLayout = getView(R.id.base_bg);

        relativeLayout.setBackgroundResource(color);
    }


    /**
     * 控件初始化
     */
    protected void initBaseView() {
        if (viewStub == null) {
            viewStub = getView(R.id.base_title_layout);
            viewStub.inflate();
            baseLeftIcon = getView(R.id.base_left_icon);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context.getApplicationContext();
        activity = (Activity) context;
    }

    /**
     * 隐藏返回键
     */
    protected void hideBack() {
        initBaseView();
        baseLeftIcon.setVisibility(View.GONE);
    }

    /**
     * 设置标题
     *
     * @param title 标题的文本
     */
    protected void setTitle(String title) {
        initBaseView();
        ((TextView) getView(R.id.base_title)).setText(title);
    }

    protected void setTitleColor(int color) {
        initBaseView();
        ((TextView) getView(R.id.base_title)).setTextColor(color);
    }

    /**
     * 设置标题
     *
     * @param titleId 标题的文本
     */
    protected void setTitle(int titleId) {
        initBaseView();
        ((TextView) getView(R.id.base_title)).setText(titleId);
    }

    /**
     * 设置返回点击事件
     *
     * @param clickListener 点击事件监听者
     */
    protected void setBaseBack(View.OnClickListener clickListener) {
        initBaseView();
        if (clickListener == null) {
            baseLeftIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
        } else {
            baseLeftIcon.setOnClickListener(clickListener);
        }
    }

    protected ImageView setBaseLeftIcon(int resId, String alertText, View.OnClickListener clickListener) {
        initBaseView();
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
    protected ImageView setBaseRightIcon1(int resId, String alertText, View.OnClickListener clickListener) {
        initBaseView();
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
    protected ImageView setBaseRightIcon2(int resId, String alertText, View.OnClickListener clickListener) {
        initBaseView();
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
    protected TextView setBaseRightText(String text, View.OnClickListener clickListener) {
        initBaseView();
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
    protected TextView setBaseRightText(int textId, View.OnClickListener clickListener) {
        initBaseView();
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
        final View view = activity.getLayoutInflater().inflate(layoutId, null);
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
    protected void hideKeyBoard() {
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
    protected void jumpTo(Class<?> clz) {
        startActivity(new Intent(context, clz));
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    protected void jumpTo(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(context, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转页面
     *
     * @param clz         所跳转的Activity类
     * @param requestCode 请求码
     */
    protected void jumpTo(Class<?> clz, int requestCode) {
        startActivityForResult(new Intent(context, clz), requestCode);
    }

    /**
     * 跳转页面
     *
     * @param clz         所跳转的Activity类
     * @param bundle      跳转所携带的信息
     * @param requestCode 请求码
     */
    protected void jumpTo(Class<?> clz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(context, clz);
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
    protected void toast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 消息提示框
     *
     * @param messageId 提示消息文本ID
     */
    protected void toast(int messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 简化获取View
     *
     * @param viewId View的ID
     * @param <T>    将View转化为对应泛型，简化强转的步骤
     * @return ID对应的View
     */
    @SuppressWarnings("unchecked")
    protected <T extends View> T getView(int viewId) {
        return (T) currentLayout.findViewById(viewId);
    }

    /**
     * 简化获取View
     *
     * @param view 父view
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
    protected abstract void getBundle();
}