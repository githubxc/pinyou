package com.xc.pinyou.utils;

import android.util.Log;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;

import static com.luck.picture.lib.tools.DebugUtil.log;

/**
 * @author smile
 * @project Util
 * @date 2016-03-01-14:55
 */
public class Util {
    public static boolean checkSdCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static void fetchUserInfo() {
        BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.i("Util","Newest UserInfo is " + s);
                } else {
                    Log.i("ERROR", "done: " + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }
}
