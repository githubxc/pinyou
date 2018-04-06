package com.xc.pinyou.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigButton;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.callback.ConfigInput;
import com.mylhyl.circledialog.params.ButtonParams;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.InputParams;
import com.mylhyl.circledialog.view.listener.OnInputClickListener;
import com.xc.pinyou.MainActivity;
import com.xc.pinyou.R;
import com.xc.pinyou.base.BaseActivity;
import com.xc.pinyou.bean.MyUser;
import com.xc.pinyou.weight.AddressPickTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import cn.qqtheme.framework.picker.SinglePicker;
import cn.qqtheme.framework.util.ConvertUtils;
import cn.qqtheme.framework.util.LogUtils;

/**
 * Created by xum19 on 2018/3/8.
 */

public class MyInfoActivity extends BaseActivity {

    private static final String TAG = "MyInfoActivity";

    @BindView(R.id.st_avatar)
    SuperTextView stAvatar;
    @BindView(R.id.st_name)
    SuperTextView stName;
    @BindView(R.id.st_sex)
    SuperTextView stSex;
    @BindView(R.id.st_location)
    SuperTextView stLocation;
    @BindView(R.id.st_sign)
    SuperTextView stSign;

    Unbinder unbinder;

    MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
    private final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择

    private String imgUrl = null, name = "姓名", sex = "性别", location = "所在地", sign = "个性签名";

    private boolean isSava = false;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_myinfo;
    }

    @Override
    protected void findViews() {

    }

    @Override
    protected void formatViews() {

    }

    @Override
    protected void formatData() {

        Glide.with(context).load(myUser.getUserAvatar().getFileUrl()).into(stAvatar.getRightIconIV());
        stName.setRightString(myUser.getUsername());
        stSex.setRightString(myUser.getSex());
        stLocation.setRightString(myUser.getLocation());
        stSign.setRightString(myUser.getSign());

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
        unbinder = ButterKnife.bind(this);

        imgUrl = myUser.getUserAvatar().getFileUrl();
        name = myUser.getUsername();
        sex = myUser.getSex();
        location = myUser.getLocation();
        sign = myUser.getSign();

        setTitle(myUser.getUsername());
        setBaseLeftIcon(R.mipmap.back, "", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });

        setBaseRightText("保存", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSava();
            }
        });
    }

    private void isSava() {
        new CircleDialog.Builder(this)
                .setCanceledOnTouchOutside(false)
                .setCancelable(false)
                .setText("确定保存？")
                .setNegative("取消", null)
                .setPositive("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       savaInfo();
                    }
                })
                .show();
    }

    @OnClick({R.id.st_avatar, R.id.st_name, R.id.st_sex, R.id.st_location, R.id.st_sign})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.st_avatar:
                changeAvatar();
                break;
            case R.id.st_name:
                new CircleDialog.Builder(this)
                        .setCanceledOnTouchOutside(true)
                        .setCancelable(true)
                        .setInputHint("请输入昵称")
                        .configInput(new ConfigInput() {
                            @Override
                            public void onConfig(InputParams params) {
                                params.inputType = InputType.TYPE_CLASS_TEXT ;
                            }
                        })
                        .setNegative("取消", null)
                        .setPositiveInput("确定", new OnInputClickListener() {
                            @Override
                            public void onClick(String text, View v) {
                                name = text;
                                stName.setRightString(text);
                                MyUser up = new MyUser();
                                up.setUsername(name);
                                up.update(myUser.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null){
                                            toast("保存成功");
                                        }else {
                                            toast("保存失败");
                                        }
                                    }
                                });
                            }
                        })
                        .show();
                break;
            case R.id.st_sex:
                selectSex();
                break;
            case R.id.st_location:
                onAddressPicker();
                break;
            case R.id.st_sign:
                new CircleDialog.Builder(this)
                        .setCanceledOnTouchOutside(true)
                        .setCancelable(true)
                        .setInputHint("请输入个性签名")
                        .configInput(new ConfigInput() {
                            @Override
                            public void onConfig(InputParams params) {
                                params.inputType = InputType.TYPE_CLASS_TEXT ;
                            }
                        })
                        .setNegative("取消", null)
                        .setPositiveInput("确定", new OnInputClickListener() {
                            @Override
                            public void onClick(String text, View v) {
                                sign = text;
                                stSign.setRightString(text);
                            }
                        })
                        .show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void changeAvatar(){
        final String[] items = {"拍照", "从相册选择"};
        new CircleDialog.Builder(this)
                .configDialog(new ConfigDialog() {
                    @Override
                    public void onConfig(DialogParams params) {
                        //增加弹出动画
                        params.animStyle = R.style.dialogWindowAnim;
                    }
                })
                .setItems(items, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int
                            position, long id) {
                        switch (position){
                            case 0:
                                PictureSelector.create(activity)
                                        .openCamera(PictureMimeType.ofAll())
                                        .maxSelectNum(1)
                                        .enableCrop(true)
                                        .compress(false)
                                        .circleDimmedLayer(true)
                                        .forResult(PHOTO_REQUEST_CAREMA);
                                break;
                            case 1:
                                PictureSelector.create(activity)
                                        .openGallery(PictureMimeType.ofAll())
                                        .maxSelectNum(1)
                                        .enableCrop(true)
                                        .compress(false)
                                        .circleDimmedLayer(true)
                                        .forResult(PHOTO_REQUEST_GALLERY);
                                break;
                        }
                    }
                })
                .setNegative("取消", null)
                .configNegative(new ConfigButton() {
                    @Override
                    public void onConfig(ButtonParams params) {
                        //取消按钮字体颜色
                        params.textColor = Color.RED;
                    }
                })
                .show();
    }

    private void savaInfo(){
        MyUser newMyuser = new MyUser();
        newMyuser.setSex(sex);
        newMyuser.setLocation(location);
        newMyuser.setSign(sign);
        newMyuser.setCredibility(myUser.getCredibility());
        newMyuser.update(myUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if ( e == null){
                    toast("更新成功！");
                }else {
                    Log.i(TAG, "done: " + e.getErrorCode() + " " + e.getMessage());
                }
            }
        });

    }

    public void selectSex() {
        List<String> data = new ArrayList<>();
        data.add("男");
        data.add("女");
        SinglePicker<String> picker = new SinglePicker<>(this, data);
        picker.setCanceledOnTouchOutside(false);
        picker.setSelectedIndex(0);
        picker.setCycleDisable(true);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String s) {
                sex = s;
                stSex.setRightString(s);
            }
        });
        picker.show();
    }

    public void onAddressPicker() {
        AddressPickTask task = new AddressPickTask(this);
        task.setHideProvince(false);
        task.setHideCounty(false);
        task.setCallback(new AddressPickTask.Callback() {
            @Override
            public void onAddressInitFailed() {
                toast("数据初始化失败");
            }

            @Override
            public void onAddressPicked(Province province, City city, County county) {
                if (county == null) {
                    location = city.getAreaName();
                    stLocation.setRightString(location);

                } else {
                    location = city.getAreaName();
                    stLocation.setRightString(location);
                }
            }
        });
        task.execute("江苏", "南京", "栖霞");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "requestCode: " + requestCode);
        if (resultCode == RESULT_OK) {
            // 图片选择结果回调
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            Log.i(TAG, "onActivityResult: " + selectList.get(0).getPath());
            LocalMedia localMedia = selectList.get(0);
            if (localMedia.isCut()){
                imgUrl = localMedia.getCutPath();
            }else {
                imgUrl = localMedia.getPath();
            }

            Glide.with(context).load(imgUrl).into(stAvatar.getRightIconIV());

            updataAvatar(imgUrl);
            // 例如 LocalMedia 里面返回三种path
            // 1.media.getPath(); 为原图path
            // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
            // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
            // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
//                    Map<String,Object> map=new HashMap<>();
//
//                    for (int i = 0; i < selectList.size(); i++){
//                        map.put("path",selectList.get(i).getPath());
//                        datas.add(map);
//                        gridViewAddImgesAdpter.notifyDataSetChanged(datas);
//                    }


        }
    }

    private void updataAvatar(String imgUrl) {

        BmobFile bmobFile = new BmobFile(new File(imgUrl));
        bmobFile.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    //Log.i(TAG, "done: " + "上传成功");
                    MyUser up = new MyUser();
                    up.setUserAvatar(bmobFile);
                    up.setCredibility(myUser.getCredibility());
                    up.update(myUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Log.i(TAG, "done: " + e.getErrorCode() + e.getMessage());
                }
            }
        });
    }
}
