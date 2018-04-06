package com.xc.pinyou.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xc.pinyou.R;
import com.xc.pinyou.SplashActivity;
import com.xc.pinyou.bean.Credibility;
import com.xc.pinyou.bean.MyUser;
import com.xc.pinyou.utils.ToastUtil;
import com.xc.pinyou.utils.Util;
import com.xc.pinyou.view.TimeButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import static java.util.Calendar.*;

/**
 * Created by xum19 on 2018/2/2.
 */

public class RegisterActivity extends Activity {

    private static final String TAG = "RegisterActivity";
    @BindView(R.id.et_register_psd)
    EditText etRegisterPsd;
    @BindView(R.id.et_register_repsd)
    EditText etRegisterRepsd;

    private Unbinder unbinder;
    private Context context;

    @BindView(R.id.et_register_phonenumber)
    EditText etRegisterPhonenumber;
    @BindView(R.id.et_register_sms)
    EditText etRegisterSms;
    @BindView(R.id.timeButton)
    TimeButton timeButton;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.tv_register_login)
    TextView tvRegisterLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        context = this;
        unbinder = ButterKnife.bind(this);

        timeButton.setTextBefore("获取验证码");
        timeButton.setTextAfter("秒后重新获取");

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSms(etRegisterPhonenumber.getText().toString());
            }
        });
    }

    @OnClick({R.id.btn_register, R.id.tv_register_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                register();
                break;
            case R.id.tv_register_login:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void register() {

        final String phone = etRegisterPhonenumber.getText().toString();
        String sms = etRegisterSms.getText().toString();

            Log.i(TAG, "register: " + phone + " "  + sms);
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(context, "请填写手机号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(sms)) {
            Toast.makeText(context, "请填写验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        if ( !((etRegisterPsd.getText().toString()).equals(etRegisterRepsd.getText().toString())) ){
            ToastUtil.showShort(context, "两次密码不一样，请重新输入");
            return;
        }

        Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.mipmap.user_avater);
        BmobFile bmobFile = new BmobFile(new File(bitmapToFile(context, bmp)));

        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    MyUser user = new MyUser();
                    user.setMobilePhoneNumber(phone);//设置手机号码（必填）
                    user.setPassword(etRegisterPsd.getText().toString()); //设置用户密码
                    user.setUserAvatar(bmobFile);
                    user.setCredibility(0);
                    user.setLocation("北京市");
                    user.setSex("男");
                    user.setSign("请输入");
                    user.signOrLogin(sms, new SaveListener<MyUser>() {

                        @Override
                        public void done(MyUser user,BmobException e) {
                            if(e==null){
                                Credibility credibility = new Credibility();
                                credibility.setCommendPeople(0);
                                credibility.setCredibility(0);
                                credibility.setUser(user);
                                credibility.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null){
                                            ToastUtil.showShort(context, "注册成功");
                                            Intent intent = new Intent(RegisterActivity.this, SplashActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            Log.i(TAG, "done: " + e.getErrorCode() + e.getMessage());
                                        }
                                    }
                                });
                            }else{
                                Log.i(TAG, "MyUserfailed: " + e.getErrorCode() + " " + e.getMessage());
                            }

                        }

                    });
                }else
                    Log.i(TAG, "bmobfileUpload: " + e.getErrorCode() + " " + e.getMessage());
            }
        });


//        BmobUser.signOrLoginByMobilePhone(phone, sms, new LogInListener<MyUser>() {
//
//            @Override
//            public void done(MyUser user, BmobException e) {
//                if(user!=null){
//                    MyUser myUser = new MyUser();
//                    myUser.setPassword(etRegisterPsd.getText().toString());
//                    myUser.setMobilePhoneNumber(phone);
//                    myUser.setMobilePhoneNumberVerified(true);
//                    myUser.signUp(new SaveListener<MyUser>() {
//                        @Override
//                        public void done(MyUser myUser, BmobException e) {
//                            if (e == null) {
//                                Intent intent = new Intent(RegisterActivity.this, SplashActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }else
//                                Log.i(TAG, "MyUserfailed: " + e.getErrorCode() + " " + e.getMessage());
//                        }
//                    });
//                }
//            }
//        });

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /*
   * bitmap转file(原图转换)
   */
    private String bitmapToFile(Context context,Bitmap bitmap) {
        String sdPath = getDiskCacheDir(context);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        String name = sdf.format(new Date()) + ".jpg";
        String picPath = sdPath + "/" + name;
        File outImage = new File(picPath);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(outImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        //返回一个图片路径
        return picPath;
    }

    /**
     * 获取缓存文件夹的相对路径
     */
    public static String getDiskCacheDir(Context ctx) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = ctx.getExternalCacheDir().getPath();
        } else {
            cachePath = ctx.getCacheDir().getPath();
        }
        return cachePath;
    }

}
