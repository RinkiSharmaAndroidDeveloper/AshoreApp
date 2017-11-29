package com.trutek.looped.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.Date;

/**
 * Created by Sandy on 12/5/2016.
 */

public class ProviderModel implements ISynchronizedModel {
    private Long id;
    private String name;
    private String phone;
    private Long recipientId;


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id=id;

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Long getRecipientId() {
        return recipientId;
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
        return null;
    }

    @Override
    public void setServerId(String id) {

    }
}
