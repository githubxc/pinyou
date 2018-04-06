package com.xc.pinyou.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xc.pinyou.R;
import com.xc.pinyou.view.TimeButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by xum19 on 2018/2/2.
 */

public class ResetPsdActivity extends Activity {

    private static final String TAG = "ResetPsdActivity";

    private Context context;

    @BindView(R.id.et_phonenumber)
    EditText etPhonenumber;
    @BindView(R.id.et_resetpsd_sms)
    EditText etResetpsdSms;
    @BindView(R.id.resetpsd_timeButton)
    TimeButton resetpsdTimeButton;
    @BindView(R.id.et_psd)
    EditText etPsd;
    @BindView(R.id.btn_resetpsd)
    Button btnResetpsd;

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resetpsd);

        context = this;

        unbinder = ButterKnife.bind(this);

        resetpsdTimeButton.setTextBefore("获取验证码");
        resetpsdTimeButton.setTextAfter("秒后重新获取");
    }

    @OnClick({R.id.resetpsd_timeButton, R.id.btn_resetpsd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.resetpsd_timeButton:
                sendSms(etPhonenumber.getText().toString());
                break;
            case R.id.btn_resetpsd:
                resetPsd();
                break;
        }
    }

    private void sendSms(String number) {
        BmobSMS.requestSMSCode(number, "Pinyou", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {

                    Log.i("bmob", number + " 短信id：" + integer);//用于查询本次短信发送详情
                } else
                    Log.i("bmob", "failed: " + e.getMessage() + " " + e.getErrorCode());
            }
        });
    }

    private void resetPsd(){
        final String sms = etResetpsdSms.getText().toString();
        final String psd = etPsd.getText().toString();

        if (TextUtils.isEmpty(sms)) {
            Toast.makeText(context, "验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(psd)) {
            Toast.makeText(context, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        BmobUser.resetPasswordBySMSCode(sms,psd, new UpdateListener() {

            @Override
            public void done(BmobException ex) {
                if(ex==null){
                    Log.i("smile", "密码重置成功");
                }else{
                    Log.i("smile", "重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
