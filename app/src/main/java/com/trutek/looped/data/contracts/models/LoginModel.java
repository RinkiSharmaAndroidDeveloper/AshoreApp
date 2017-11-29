package com.trutek.looped.data.contracts.models;

import com.trutek.looped.msas.common.models.LoginType;

import java.io.Serializable;

public class LoginModel implements Serializable{

    public String phone;
    public String pin;
    public LoginType type;
    public DeviceModel device;

    public LoginModel (){

    }

    public LoginModel(String phone, String pin) {
        this.phone = phone;
        this.pin = pin;
    }

}
