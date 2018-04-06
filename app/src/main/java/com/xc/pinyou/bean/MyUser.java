package com.xc.pinyou.bean;

import android.content.Intent;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by xum19 on 2018/1/11.
 */

public class MyUser extends BmobUser {

    private BmobFile userAvatar;//用户头像

    private float credibility;//信誉度

    private String sex;//性别

    private String location;//所在地

    private String sign;//个性签名

    private Integer commendPeople;//评论我的人

    public BmobFile getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(BmobFile userAvatar) {
        this.userAvatar = userAvatar;
    }

    public float getCredibility() {
        return credibility;
    }

    public void setCredibility(float credibility) {
        this.credibility = credibility;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Integer getCommendPeople() {
        return commendPeople;
    }

    public void setCommendPeople(Integer commendPeople) {
        this.commendPeople = commendPeople;
    }
}
