package com.trutek.looped.data.contracts.models;

import android.graphics.Bitmap;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.io.Serializable;
import java.util.Date;

public class ContactModel implements Serializable {

    public Long id;
    public String name;
    public String number;
    public String email;
    public boolean isSelected;
    public Bitmap thumb;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }
}
