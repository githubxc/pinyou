package com.xc.pinyou.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * Created by xum19 on 2018/3/7.
 */

public class Success extends BmobObject {

    private Carpool authorId;

    private MyUser carpoolerId;

    private Boolean isCommend;

    public Success(){

    }

    public Success(Carpool authorId, MyUser carpoolerId, boolean isCommend) {
        this.authorId = authorId;
        this.carpoolerId = carpoolerId;
        this.isCommend = isCommend;
    }

    public Carpool getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Carpool authorId) {
        this.authorId = authorId;
    }

    public MyUser getCarpoolerId() {
        return carpoolerId;
    }

    public void setCarpoolerId(MyUser carpoolerId) {
        this.carpoolerId = carpoolerId;
    }

    public Boolean getCommend() {
        return isCommend;
    }

    public void setCommend(Boolean commend) {
        isCommend = commend;
    }
}
