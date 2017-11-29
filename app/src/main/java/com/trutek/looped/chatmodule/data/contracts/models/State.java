package com.trutek.looped.chatmodule.data.contracts.models;

/**
 * Created by msas on 9/16/2016.
 */
public enum State {

    DELIVERED(0),
    READ(1),
    SYNC(2),
    TEMP_LOCAL(3),
    TEMP_LOCAL_UNREAD(4);

    private int code;

    State(int code) {
        this.code = code;
    }

    public static State parseByCode(int code) {
        State[] valuesArray = State.values();
        State result = null;
        for (State value : valuesArray) {
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
