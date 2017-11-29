package com.trutek.looped.gcm;

import java.io.Serializable;

public class PushNotificationModel implements Serializable{

    public Data a;
    public String i;

    public class Data{

        public String action;
        public String api;
        public String id;
        public Entity entity;
    }

    public class Entity{
        public String id;
        public String type;

    }

}
