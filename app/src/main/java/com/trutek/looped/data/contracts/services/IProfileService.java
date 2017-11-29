package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.ContactModel;
import com.trutek.looped.data.contracts.models.InviteModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.List;


public interface IProfileService extends ICRUDService<ProfileModel>{

    void saveProfileToDatabase(ProfileModel profileModel);

    void updateProfile(ProfileModel profileModel, AsyncResult<ProfileModel> result);

    void updateProfileRemote(ProfileModel profileModel, AsyncResult<ProfileModel> result);

    void updateInterest(ProfileModel profileModel, AsyncResult<ProfileModel> result);

    void updateSavedProfile(ProfileModel profileModel);

    void discoverProfiles(PageInput input, AsyncResult<Page<ProfileModel>> result);

    void discoverProfilesForFilter(String query, AsyncResult<Page<ProfileModel>> result);

    void sendInvitation(InviteModel model, AsyncResult<InviteModel> result);

    void getProfile(String id, AsyncResult<ProfileModel> result);

    ProfileModel getMyProfile(AsyncResult<ProfileModel> result);

    ProfileModel getUpdatedProfiles(AsyncResult<ProfileModel> result);

    void fetchMyProfile(PageInput pageInput);

    List<ProfileModel> getMyConnections(AsyncResult<Page<ProfileModel>> result);

    void clearMyProfile();


//    void getMyProfile(AsyncResult<ProfileModel> result);
}
