package com.trutek.looped.data.contracts.models;


import java.io.Serializable;

public class ChatModel implements Serializable{

    public int id;
    public String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
