package com.trutek.looped.data.contracts.models;

import com.trutek.looped.msas.common.contracts.ISynchronizedModel;
import com.trutek.looped.msas.common.models.ModelState;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by msas on 10/7/2016.
 */
public class MemberModel implements Serializable {

    public Date date;
    public String status;

    public ProfileModel getProfile() {
        return profile;
    }

    public void setProfile(ProfileModel profile) {
        this.profile = profile;
    }

    public ProfileModel profile=new ProfileModel();

}
