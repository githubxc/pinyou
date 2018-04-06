package com.xc.pinyou.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mylhyl.circledialog.CircleDialog;
import com.xc.pinyou.R;
import com.xc.pinyou.adapter.MyCarpoolAdapter;
import com.xc.pinyou.adapter.MyCommendAdapter;
import com.xc.pinyou.base.BaseActivity;
import com.xc.pinyou.bean.Carpool;
import com.xc.pinyou.bean.Commend;
import com.xc.pinyou.bean.Credibility;
import com.xc.pinyou.bean.MyUser;
import com.xc.pinyou.bean.Success;
import com.xc.pinyou.event.CarpoolEvent;
import com.xc.pinyou.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.b.V;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MyCommendActivity extends BaseActivity {

    private static final String TAG = "MyCommendActivity";

    private List<Carpool> carpoolList, c;
    private List<Success> successList;
    private MyCommendAdapter adapter;

    private RecyclerView recyclerView;

    MyUser myUser = BmobUser.getCurrentUser(MyUser.class);

    private float commendRat = 0;

    private boolean flag1 = false, flag2 = false;
    private List<String> commendStr = new ArrayList<>();
    private String commendStr1, commendStr2;

    private Activity activity;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_commend;
    }

    @Override
    protected void findViews() {
        recyclerView = getView(R.id.rv_commend);
    }

    @Override
    protected void formatViews() {

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

    }

    @Override
    protected void formatData() {
        successList = new ArrayList<>();
        queryMyCarpool();

    }

    @Override
    protected void getBundle(Bundle bundle) {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation

        activity = this;

        setTitle("我的评价");
        setBaseLeftIcon(R.mipmap.back, "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void queryMyCarpool() {
        carpoolList = new ArrayList<>();
        carpoolList.clear();
        BmobQuery<Success> query = new BmobQuery<>();
        query.include("carpoolerId,authorId");
        query.findObjects(new FindListener<Success>() {
            @Override
            public void done(List<Success> list, BmobException e) {
                if ( e == null){

                    for (Success success : list){

                        if ((success.getCarpoolerId().getObjectId()).equals(myUser.getObjectId())){
                            successList.add(success);
                            carpoolList.add(success.getAuthorId());
                            getCarpoolInfo(success.getAuthorId().getObjectId());
                        }

                    }
                }else {
                    Log.i(TAG, "done: " + e.getMessage() + " " + e.getErrorCode());
                }
            }
        });

    }

    private void getCarpoolInfo(String id){
        c = new ArrayList<>();
        c.clear();
        BmobQuery<Carpool> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", id);
        query.include("carpoolId,credibility");
        query.findObjects(new FindListener<Carpool>() {
            @Override
            public void done(List<Carpool> list, BmobException e) {
                if (e == null){
                    for (Carpool carpool:list){
                        c.add(carpool);
                        Log.i(TAG, "done: " + carpool.getObjectId() + " " +
                                carpool.getCarpoolId().getObjectId() + " " +
                                carpool.getCarpoolId().getUsername());
                    }
                    adapter = new MyCommendAdapter(R.layout.my_commend_recycle_item, c, context);
                    adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                            if (successList.get(position).getCommend()){
                                new CircleDialog.Builder((FragmentActivity) activity)
                                        .setTitle("提示")
                                        .setText("你已评价过该订单，不能重复评价")
                                        .setPositive("确定", null)
                                        .show();
                            }else {
                                toCommend(c.get(position));
                            }

                        }
                    });
                    recyclerView.setAdapter(adapter);
                }else {
                    Log.i(TAG, "错误1: " + e.getMessage() + " " + e.getErrorCode());
                }
            }
        });
    }

    private void toCommend(Carpool carpool){

        Log.i(TAG, "获取拼车人信息: " + carpool.getCarpoolId().getUsername());

        Dialog dialog = new Dialog(this,R.style.ActionSheetDialogStyle);
        View  inflate = LayoutInflater.from(this).inflate(R.layout.commend_dialog, null);

        ImageView back = inflate.findViewById(R.id.iv_back);
        RatingBar rb = inflate.findViewById(R.id.rb_commend);
        TextView commend1, commend2;
        commend1 = inflate.findViewById(R.id.tv_1);
        commend2 = inflate.findViewById(R.id.tv_2);
        Button btnCommend = inflate.findViewById(R.id.btn_commend);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        rb.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                commendRat = v;
            }
        });

        commend1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag1){
                    commend1.setBackgroundResource(R.drawable.text_black_stroke);
                    commend1.setTextColor(ContextCompat.getColor(context, R.color.white));
                    commendStr1 = commend1.getText().toString();
                }else {
                    commend1.setBackgroundResource(R.drawable.text_stroke);
                    commend1.setTextColor(ContextCompat.getColor(context, R.color.black));
                    commendStr1 = null;
                }
                commendStr.add(commendStr1);
                flag1 = !flag1;
            }
        });

        commend2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag2){
                    commend2.setBackgroundResource(R.drawable.text_black_stroke);
                    commend2.setTextColor(ContextCompat.getColor(context, R.color.white));
                    commendStr2 = commend2.getText().toString();
                }else {
                    commend2.setBackgroundResource(R.drawable.text_stroke);
                    commend2.setTextColor(ContextCompat.getColor(context, R.color.black));
                    commendStr2 = null;
                }
                commendStr.add(commendStr2);
                flag2 = !flag2;
            }
        });

        btnCommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commend commend = new Commend();
                commend.setGrade(commendRat);
                commend.setCommendStr(commendStr);
                commend.setCarpooId(carpool);
                commend.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null){
                           setUserGrade(carpool.getCarpoolId());
                           setSuccess(carpool.getObjectId());
                        }else {
                            Log.i(TAG, "错误2: " + e.getMessage() + " " + e.getErrorCode());
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();//显示对话框
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity( Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = d.widthPixels; // 宽度设置为屏幕的0.8
        lp.height = (int) (d.heightPixels * 0.6);
        dialogWindow.setAttributes(lp);
    }

    private void setSuccess(String id) {

        BmobQuery<Success> query = new BmobQuery<>();
        query.addWhereEqualTo("authorId", id);
        query.include("authorId");
        query.findObjects(new FindListener<Success>() {
            @Override
            public void done(List<Success> list, BmobException e) {
                if (e == null){
                    for (Success s : list){
                        Log.i(TAG, "getAuthorId: " + s.getAuthorId());
                        Log.i(TAG, "getAuthorId.getObjectId(): " + s.getAuthorId().getObjectId());
                        Log.i(TAG, "id: " + id + "s.getObjectId()" + s.getObjectId());
                        if ((s.getAuthorId().getObjectId()).equals(id)){
                            Success success = new Success();
                            success.setCommend(true);
                            success.update(s.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null){
                                        Log.i(TAG, "done: " + "保存成功");
                                    }else {
                                        Log.i(TAG, "Success: " + e.getErrorCode() + e.getMessage());
                                    }
                                }
                            });
                        }
                    }

                }else {
                    Log.i(TAG, "Success:authorId " + e.getMessage() + e.getErrorCode());
                }
            }
        });

    }

    private void setUserGrade(MyUser id){

        BmobQuery<Credibility> query = new BmobQuery<>();
        query.include("user");
        query.findObjects(new FindListener<Credibility>() {
            @Override
            public void done(List<Credibility> list, BmobException e) {
                if (e == null){
                    for (Credibility credibility:list){
                        if ((credibility.getUser().getObjectId()).equals(id.getObjectId())){

                            Integer str = Integer.valueOf(credibility.getCommendPeople());
                            int i = str.intValue();
                            float rr = (float)i;

                            float score = (credibility.getCredibility() * i + commendRat)/(rr + 1);

                            credibility.setCredibility(score);
                            credibility.setCommendPeople(++i);
                            credibility.update(credibility.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null){
                                        toast("评价成功！");
                                    }else {

                                        toast("评价失败！");
                                        Log.i(TAG, "错误3: " + e.getMessage() + e.getErrorCode());
                                    }
                                }
                            });
                        }else {
                            //toast("评价失败！");
                        }
                    }

                }else {
                    Log.i(TAG, "错误4: " + e.getErrorCode() + e.getMessage());
                }
            }
        });
    }
}
