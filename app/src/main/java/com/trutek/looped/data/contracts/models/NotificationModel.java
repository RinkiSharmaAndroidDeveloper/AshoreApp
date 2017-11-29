package com.trutek.looped.data.contracts.models;

import android.graphics.Bitmap;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.Date;

public class NotificationModel implements ISynchronizedModel{

    public Long localId;
    public Date date;
    public String subject;
    public String message;
    public String id;

    public Data data;

    @Override
    public Long getId() {
        return localId;
    }

    @Override
    public void setId(Long id) {
        this.localId = localId;
    }

    @Override
    public Date getTimeStamp() {
        return null;
    }

    @Override
    public void setTimeStamp(Date timeStamp) {

    }

    @Override
    public ModelState getStatus() {
        return null;
    }

    @Override
    public void setStatus(Integer status) {

    }

    @Override
    public String getServerId() {
        return id;
    }

    @Override
    public void setServerId(String id) {
        this.id = id;
    }

    public class Data{

        public String api;
        public String action;
        public Entity entity;

    }

    public class Entity {

        public String id;
        public String type;
        public String picUrl;

    }
}
