package com.xc.pinyou.event;

import android.util.Log;

import com.xc.pinyou.bean.Carpool;

/**
 * Created by xum19 on 2018/2/19.
 */

public class CarpoolEvent {

    private static final String TAG = "MessageEvent";

    public Carpool carpool;

    public String tagId;

    public CarpoolEvent(Carpool carpool, String tagId) {
        this.carpool = carpool;
        this.tagId = tagId;
    }

    public Carpool getCarpool() {
        return carpool;
    }

    public String getTagId() {
        return tagId;
    }
}