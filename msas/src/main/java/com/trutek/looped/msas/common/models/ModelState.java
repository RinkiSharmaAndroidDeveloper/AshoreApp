package com.trutek.looped.msas.common.models;

public enum ModelState {
    synced(0),
    created(1),
    updated(2),
    deleted(3);

    private final int state;

    ModelState(int state) {
        this.state = state;
    }

    public int getValue() {
        return this.state;
    }

    public static ModelState fromInt(int i) {
        switch (i) {
            case 0:
                return synced;
            case 1:
                return created;
            case 2:
                return updated;
            case 3:
                return deleted;
            default:
                return synced;
        }
    }
}
