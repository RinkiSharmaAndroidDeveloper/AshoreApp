package com.trutek.looped.msas.common.models;

public enum LoginType {

    EMAIL(0),
    FACEBOOK(1),
    MOBILE(2);

    private final int type;

    LoginType(int type) {
        this.type = type;
    }

    public int getValue() {
        return this.type;
    }

    public static LoginType fromInt(int i) {
        switch (i) {
            case 0:
                return EMAIL;
            case 1:
                return FACEBOOK;
            case 2:
                return MOBILE;
            default:
                return EMAIL;
        }
    }
}
