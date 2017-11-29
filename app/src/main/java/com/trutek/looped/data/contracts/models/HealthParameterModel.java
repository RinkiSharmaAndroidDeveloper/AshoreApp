package com.trutek.looped.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Amrit on 01/12/16.
 */
public class HealthParameterModel implements ISynchronizedModel {

    Long localId;
    String id;
    String name;
    ArrayList<String> units;
    public boolean isSelected;
    @Override
    public Long getId() {
        return localId;
    }

    @Override
    public void setId(Long id) {
        localId = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getUnits() {
        return units;
    }

    public void setUnits(ArrayList<String> units) {
        this.units = units;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }
}
