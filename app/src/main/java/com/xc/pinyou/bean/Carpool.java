package com.xc.pinyou.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;

/**
 * Created by xum19 on 2018/2/15.
 */

public class Carpool extends BmobObject {
    private MyUser carpoolId;//拼车ID
    private Credibility credibility;
    private Double startPointLat;//起点维度
    private Double startPointLon;//起点经度
    private Double endPointLat;//终点维度
    private Double endPointLon;//终点经度
    private String startPoint;
    private String endPoint;
    private BmobDate startTime;//拼车时间
    private Integer people;//人数
    private String money;//车费

    public MyUser getCarpoolId() {
        return carpoolId;
    }

    public void setCarpoolId(MyUser carpoolId) {
        this.carpoolId = carpoolId;
    }

    public Credibility getCredibility() {
        return credibility;
    }

    public void setCredibility(Credibility credibility) {
        this.credibility = credibility;
    }

    public Double getStartPointLat() {
        return startPointLat;
    }

    public void setStartPointLat(Double startPointLat) {
        this.startPointLat = startPointLat;
    }

    public Double getStartPointLon() {
        return startPointLon;
    }

    public void setStartPointLon(Double startPointLon) {
        this.startPointLon = startPointLon;
    }

    public Double getEndPointLat() {
        return endPointLat;
    }

    public void setEndPointLat(Double endPointLat) {
        this.endPointLat = endPointLat;
    }

    public Double getEndPointLon() {
        return endPointLon;
    }

    public void setEndPointLon(Double endPointLon) {
        this.endPointLon = endPointLon;
    }

    public BmobDate getStartTime() {
        return startTime;
    }

    public void setStartTime(BmobDate startTime) {
        this.startTime = startTime;
    }

    public Integer getPeople() {
        return people;
    }

    public void setPeople(Integer people) {
        this.people = people;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }
}
