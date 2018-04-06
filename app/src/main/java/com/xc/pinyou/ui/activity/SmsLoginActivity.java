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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by xum19 on 2018/2/2.
 */

public class SmsLoginActivity extends Activity {

    private static final String TAG = "SmsLoginActivity";

    private Unbinder unbinder;

    @BindView(R.id.et_phonenumber)
    EditText etPhonenumber;
    @BindView(R.id.et_login_sms)
    EditText etLoginSms;
    @BindView(R.id.login_timeButton)
    TimeButton loginTimeButton;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_login_psd)
    TextView tvLoginPsd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_sms);
        unbinder = ButterKnife.bind(this);

        loginTimeButton.setTextBefore("获取验证码");
        loginTimeButton.setTextAfter("秒后重新获取");
    }

    @OnClick({R.id.login_timeButton, R.id.btn_login, R.id.tv_login_psd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_timeButton:
                sendSms(etPhonenumber.getText().toString());
                break;
            case R.id.btn_login:
                Log.i(TAG, "onViewClicked: " + "去登录了");
                login();
                break;
            case R.id.tv_login_psd:
                Intent intent = new Intent(SmsLoginActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void login(){
        final String phone = etPhonenumber.getText().toString();
        String sms = etLoginSms.getText().toString();

        BmobQuery<MyUser> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<MyUser>() {
            @Override
            public void done(List<MyUser> list, BmobException e) {
                if (e == null){
                    if (list.size() == 0){
                        Log.i(TAG, "done: " + "去注册了");
                        register(sms, phone);
                    }else {
                        for (MyUser user:list){
                            if ((user.getUsername()).equals(phone) || (user.getMobilePhoneNumber()).equals(phone)){
                                BmobUser.loginBySMSCode(phone, sms, new LogInListener<MyUser>(){
                                    @Override
                                    public void done(MyUser user, BmobException e) {
                                        if(user!=null){
                                            Log.i("smile","用户登陆成功");
                                            Intent intent = new Intent(SmsLoginActivity.this, SplashActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            Log.i(TAG, "MyUser: 用户登陆失败" + e.getMessage() + e.getErrorCode());
                                        }
                                    }
                                });

                            }else {
                                Log.i(TAG, "list大于0: " + "去注册了");
                                register(sms, phone);
                            }
                        }
                    }

                }else {
                    Log.i(TAG, "done: " + e.getErrorCode() + e.getMessage());
                }

            }
        });
//        BmobSMS.verifySmsCode(phone, sms, new UpdateListener() {
//            @Override
//            public void done(BmobException e) {
//                if (e == null) {
//                    BmobQuery<MyUser> bmobQuery = new BmobQuery<>();
//                    bmobQuery.findObjects(new FindListener<MyUser>() {
//                        @Override
//                        public void done(List<MyUser> list, BmobException e) {
//
//                            for (MyUser user:list){
//                                if ((user.getUsername()).equals(phone) || (user.getMobilePhoneNumber()).equals(phone)){
//                                    Intent intent = new Intent(SmsLoginActivity.this, SplashActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                } else {
//                                   savaUser(phone);
//                                }
//                            }
//
//                        }
//                    });
//
//                } else
//                    Log.i(TAG, "failed: " + e.getErrorCode() + " " + e.getMessage());
//            }
//        });
    }

    private void register(String sms, String phone) {

        if (TextUtils.isEmpty(sms)) {
            Toast.makeText(SmsLoginActivity.this, "请填写验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.mipmap.user_avater);
        BmobFile bmobFile = new BmobFile(new File(bitmapToFile(getApplicationContext(), bmp)));

        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                    if (e == null) {
                        Log.i(TAG, "done: " + "上传头像成功了");
                    MyUser user = new MyUser();
                    user.setUsername(phone);
                    user.setMobilePhoneNumber(phone);//设置手机号码（必填）
                    user.setPassword(phone); //设置用户密码
                    user.setUserAvatar(bmobFile);
                    user.setCredibility(0);
                    user.setLocation("北京市");
                    user.setSex("男");
                    user.setSign("请输入");
                    user.signOrLogin(sms, new SaveListener<MyUser>() {

                        @Override
                        public void done(MyUser user, BmobException e) {
                            if (e == null) {
                                Log.i(TAG, "done: " + "创建用户成功");
                                Credibility credibility = new Credibility();
                                credibility.setCommendPeople(0);
                                credibility.setCredibility(0);
                                credibility.setUser(user);
                                credibility.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null) {
                                            Log.i(TAG, "done: " + "创建信誉度成功");
                                            ToastUtil.showShort(SmsLoginActivity.this, "注册成功");
                                            Intent intent = new Intent(SmsLoginActivity.this, SplashActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Log.i(TAG, "done: " + e.getErrorCode() + e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                Log.i(TAG, "MyUserfailed: " + e.getErrorCode() + " " + e.getMessage());
                            }

                        }

                    });
                } else
                    Log.i(TAG, "bmobfileUpload: " + e.getErrorCode() + " " + e.getMessage());
            }
        });
    }

    private void savaUser(String phone) {
        Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.mipmap.user_avater);
        BmobFile bmobFile = new BmobFile(new File(bitmapToFile(getApplicationContext(), bmp)));

        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    MyUser user = new MyUser();
                    user.setUsername(phone);
                    user.setMobilePhoneNumber(phone);//设置手机号码（必填）
                    user.setPassword(phone); //设置用户密码
                    user.setUserAvatar(bmobFile);
                    user.setCredibility(0);
                    user.setLocation("北京市");
                    user.setSex("男");
                    user.setSign("请输入");
                    user.save(new SaveListener<MyUser>() {

                        @Override
                        public void done(MyUser user, BmobException e) {
                            if (e == null) {
                                Credibility credibility = new Credibility();
                                credibility.setCommendPeople(0);
                                credibility.setCredibility(0);
                                credibility.setUser(user);
                                credibility.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null) {
                                            ToastUtil.showShort(SmsLoginActivity.this, "注册成功");
                                            Intent intent = new Intent(SmsLoginActivity.this, SplashActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Log.i(TAG, "done: " + e.getErrorCode() + e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                Log.i(TAG, "MyUserfailed: " + e.getErrorCode() + " " + e.getMessage());
                            }

                        }

                    });
                } else
                    Log.i(TAG, "bmobfileUpload: " + e.getErrorCode() + " " + e.getMessage());
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
