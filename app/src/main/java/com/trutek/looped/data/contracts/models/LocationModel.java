package com.trutek.looped.data.contracts.models;


import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.ArrayList;
import java.util.Date;

public class LocationModel implements ISynchronizedModel{

    public Long localId;
    public String name;
    public String description;
    public String location;
    String country;
    boolean isAutoDetect = false;
    String placeId;
    public ArrayList<String> coordinates = new ArrayList<>();

    public String id;
    public java.util.Date timeStamp;
    public Integer syncStatus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Long getId() {
        return localId;
    }

    @Override
    public void setId(Long id) {
        this.localId = id;
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
        return id;
    }

    @Override
    public void setServerId(String id) {
        this.id = id;
    }


    private class Coordinate{

        public ArrayList<String> type;
        public String index;

        public String latValue;
        public String longValue;

        public void setLatValue(String latValue) {
            this.latValue = latValue;
            setType();
        }

        public void setLongValue(String longValue) {
            this.longValue = longValue;
            setType();
        }

        public void setType() {
            ArrayList<String> location = new ArrayList<>();
            location.add(longValue);
            location.add(latValue);
            this.type = location;
        }
    }

    public boolean isAutoDetect() {
        return isAutoDetect;
    }

    public void setAutoDetect(boolean autoDetect) {
        isAutoDetect = autoDetect;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
