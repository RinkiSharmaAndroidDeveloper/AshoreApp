package com.trutek.looped.chatmodule.data.contracts.models;


import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.Date;

public class AttachmentModel implements ISynchronizedModel {

    public Long id;
    public String attachmentId;
    public Type type;
    public String name;
    public Double size;
    public String URL;
    public String additionalInfo;
    public java.util.Date timeStamp;
    public Integer syncStatus;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getTimeStamp() {
        return timeStamp;
    }

    @Override
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public ModelState getStatus() {
        return ModelState.fromInt(syncStatus);
    }

    @Override
    public void setStatus(Integer status) {
        this.syncStatus = status;
    }

    @Override
    public String getServerId() {
        return attachmentId;
    }

    @Override
    public void setServerId(String id) {
        this.attachmentId = id;
    }


    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Double getSize() {
        return size;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getURL() {
        return URL;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {

        AUDIO(0),
        VIDEO(1),
        PHOTO(2),
        DOC(3),
        OTHER(4);

        private int code;

        Type(int code) {
            this.code = code;
        }

        public static Type parseByCode(int code) {
            Type[] valuesArray = Type.values();
            Type result = null;
            for (Type value : valuesArray) {
                if (value.getCode() == code) {
                    result = value;
                    break;
                }
            }
            return result;
        }

        public int getCode() {
            return code;
        }
    }

}
