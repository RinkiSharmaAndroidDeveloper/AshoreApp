package com.trutek.looped.ui.recipient;

/**
 * Created by Sandy on 12/1/2016.
 */

public class RecipientDashModel {

    String name, number;

    public RecipientDashModel(String name, String number) {
        this.name = name;
        this.number = number;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
