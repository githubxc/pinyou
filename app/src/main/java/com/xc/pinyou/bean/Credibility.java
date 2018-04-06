package com.xc.pinyou.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by xum19 on 2018/3/10.
 */

public class Credibility extends BmobObject {

    private float credibility;

    private MyUser user;

    private Integer commendPeople;

    public Credibility() {
    }

    public float getCredibility() {
        return credibility;
    }

    public void setCredibility(float credibility) {
        this.credibility = credibility;
    }

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public Integer getCommendPeople() {
        return commendPeople;
    }

    public void setCommendPeople(Integer commendPeople) {
        this.commendPeople = commendPeople;
    }
}
