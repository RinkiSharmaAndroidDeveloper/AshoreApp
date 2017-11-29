package com.trutek.looped.data.contracts.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by msas on 9/28/2016.
 */
public class InviteModel implements Serializable{

    public List<ContactModel> contacts = new ArrayList<>();
    public ArrayList<ConnectionModel> connections = new ArrayList<>();

}
