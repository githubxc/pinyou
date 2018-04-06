package com.xc.pinyou.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xc.pinyou.R;
import com.xc.pinyou.SplashActivity;
import com.xc.pinyou.bean.MyUser;
import com.xc.pinyou.chat.model.UserModel;
import com.xc.pinyou.utils.ToastUtil;
import com.xc.pinyou.utils.Util;
import com.yanzhenjie.alertdialog.AlertDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by xum19 on 2018/2/2.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private static int permissionRequestCode = 200;

    private Context context;

    private Unbinder unbinder;

    @BindView(R.id.et_phonenumber)
    EditText etPhonenumber;
    @BindView(R.id.et_psd)
    EditText etPsd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_login_sms)
    TextView tvLoginSms;
    @BindView(R.id.tv_forget_psd)
    TextView tvForgetPsd;
    @BindView(R.id.tv_register)
    TextView tvRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_psd);

        context = this;
        unbinder = ButterKnife.bind(this);

        MyUser userInfo = BmobUser.getCurrentUser(MyUser.class);

        if (userInfo != null){
            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        }

        initPermission();
    }
//
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus && Build.VERSION.SDK_INT >= 19)
//        {
//
//            View decorView = getWindow().getDecorView();
//
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                            View.SYSTEM_UI_FLAG_FULLSCREEN |
//                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//
//    }

    @OnClick({R.id.btn_login, R.id.tv_login_sms, R.id.tv_forget_psd, R.id.tv_register})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login(etPhonenumber.getText().toString(),
                        etPsd.getText().toString());
                break;
            case R.id.tv_login_sms:
                Intent intent = new Intent(LoginActivity.this, SmsLoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_forget_psd:
                startActivity(new Intent(LoginActivity.this, ResetPsdActivity.class));
                finish();
                break;
            case R.id.tv_register:

                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void login(String phone, String psd){

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(context, "请填写手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(psd)) {
            Toast.makeText(context, "请填写密码", Toast.LENGTH_SHORT).show();
            return;
        }

        BmobUser.loginByAccount(phone, psd, new LogInListener<MyUser>() {
            @Override
            public void done(MyUser myUser, BmobException e) {
                if ( e == null){
                    Intent intent=new Intent(LoginActivity.this,SplashActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Log.i(TAG, "loginFail: " + e.getMessage() + " " + e.getErrorCode());
                    ToastUtil.showShort(context, "登录失败");
                }
            }
        });
    }

    /**
     * 申请权限
     */
    private void initPermission(){
        AndPermission.with(this)
                .requestCode(permissionRequestCode)
                .permission( Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CHANGE_NETWORK_STATE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.VIBRATE)
                .rationale(rationaleListener)
                .callback(listener)
                .start();

    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。

            // 这里的requestCode就是申请时设置的requestCode。
            // 和onActivityResult()的requestCode一样，用来区分多个不同的请求。
            if (requestCode == permissionRequestCode) {
                // TODO ...
//                Intent intent=new Intent(LoginActivity.this,LoginActivity.class);
//                startActivity(intent);
//                finish();
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            if (requestCode == permissionRequestCode) {
                Log.i(TAG, "onFailed: " + deniedPermissions.toString());
                ToastUtil.showShort(context, "没获得权限: " + deniedPermissions.get(0) + " " + deniedPermissions.get(1));
            }
        }
    };

        /**
         * Rationale支持，这里自定义对话框。
         */
        private RationaleListener rationaleListener = (requestCode, rationale) -> {
            AlertDialog.newBuilder(context)
                    .setTitle("友好提醒")
                    .setMessage("你已拒绝权限，沒有权限无法使用！")
                    .setPositiveButton("允许", (dialog, which) -> {
                        rationale.resume();
                    })
                    .setNegativeButton("拒绝", (dialog, which) -> {
                        rationale.cancel();
                    }).show();
        };

}
