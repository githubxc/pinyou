package com.xc.pinyou.chat;

/**
 * Created by xum19 on 2018/3/5.
 */

public class MyChatEvent {

    private String id;

    private String objectId;

    private String avatarUrl;

    private String userName;

    public MyChatEvent(String id, String objectId, String avatarUrl, String userName) {
        this.id = id;
        this.objectId = objectId;
        this.avatarUrl = avatarUrl;
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getUserName() {
        return userName;
    }
}
