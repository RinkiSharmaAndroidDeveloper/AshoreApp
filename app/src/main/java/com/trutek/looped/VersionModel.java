package com.trutek.looped;

/**
 * Created by Suleiman on 14-04-2015.
 */
public class VersionModel {
    public String name;

    public static final String[] data = {"Cupcake", "Donut", "Eclair",
            "Froyo", "Gingerbread", "Honeycomb",
            "Icecream Sandwich", "Jelly Bean", "Kitkat", "Lollipop"};

    public static final String[] topics = {"Resource", "Giving Tips", "Social",
            "Activity Group"};


    VersionModel(String name){
        this.name=name;
    }
}

