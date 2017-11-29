package com.trutek.looped.data.contracts.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by msas on 10/19/2016.
 */
public class FilterModel implements Serializable{

    public ArrayList<InterestModel> interests = new ArrayList<>();
    public ArrayList<TagModel> tags = new ArrayList<>();

}
