package com.trutek.looped.chatmodule.data.contracts.models;

public enum NotificationType {

    GROUP_CHAT_CREATE(1),
    GROUP_CHAT_UPDATE(2);

    private int value;

    NotificationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static NotificationType parseByValue(int value) {
        NotificationType[] prioritiesArray = NotificationType.values();
        NotificationType result = null;
        for (NotificationType type : prioritiesArray) {
            if (type.getValue() == value) {
                result = type;
                break;
            }
        }
        return result;
    }
}
