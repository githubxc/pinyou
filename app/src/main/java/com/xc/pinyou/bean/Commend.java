package com.xc.pinyou.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by xum19 on 2018/3/9.
 */

public class Commend extends BmobObject {

    private Carpool carpooId;

    private List<String> commendStr;

    private float grade;

    public Commend() {
    }

    public Carpool getCarpooId() {
        return carpooId;
    }

    public List<String> getCommendStr() {
        return commendStr;
    }

    public float getGrade() {
        return grade;
    }

    public void setCarpooId(Carpool carpooId) {
        this.carpooId = carpooId;
    }

    public void setCommendStr(List<String> commendStr) {
        this.commendStr = commendStr;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }
}
