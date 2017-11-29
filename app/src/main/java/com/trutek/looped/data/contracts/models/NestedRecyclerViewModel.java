package com.trutek.looped.data.contracts.models;

/**
 * Created by Amrit on 25/02/17.
 */

public class NestedRecyclerViewModel {

    int parentPosition;
    int childPosition;

    public int getParentPosition() {
        return parentPosition;
    }

    public void setParentPosition(int parentPosition) {
        this.parentPosition = parentPosition;
    }

    public int getChildPosition() {
        return childPosition;
    }

    public void setChildPosition(int childPosition) {
        this.childPosition = childPosition;
    }
}
