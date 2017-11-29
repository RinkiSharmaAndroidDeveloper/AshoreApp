package com.trutek.looped.data.impl.services;

import android.util.Log;

import com.trutek.looped.data.contracts.models.CategoryModel;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.InviteModel;
import com.trutek.looped.data.contracts.models.LocationModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.impl.entities.Profile;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;
import com.trutek.looped.data.contracts.apis.IProfileApi;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IProfileService;

import java.util.List;

public class ProfileService extends BaseService<ProfileModel> implements IProfileService {
    final String TAG = ProfileService.class.getSimpleName();

    private IRepository<RecipientModel> localRecipient;
    private IRepository<InterestModel> localIntererest;
    private IRepository<TagModel> localTag;
    private IProfileApi<ProfileModel> profileRemote;
    private IRepository<CategoryModel> localCategory;

    public ProfileService(IRepository<ProfileModel> local,  IRepository<RecipientModel> localRecipient,
                          IRepository<InterestModel> localIntererest,IRepository<TagModel> localTag,
                          IProfileApi<ProfileModel> profileRemote, IRepository<CategoryModel> localCategory) {
        super(local);
        this.localRecipient = localRecipient;
        this.localIntererest = localIntererest;
        this.localTag = localTag;
        this.profileRemote = profileRemote;
        this.localCategory = localCategory;
    }

    @Override
    public void saveProfileToDatabase(ProfileModel profileModel) {
        Log.d(TAG, profileModel.toString());

        ProfileModel model = getByServerId(profileModel.getServerId());

        if(null == model) {
            profileModel.setMine(true);
            profileModel.setId(_local.create(profileModel, Constants.BROADCAST_MY_PROFILE_VIEW).getId());

            if (null != profileModel.getTags() && profileModel.getTags().size() > 0) {

                for (TagModel tagModel : profileModel.getTags()) {
                    tagModel.setProfileId(profileModel.getId());
                    tagModel.setSelected(true);
                    localTag.create(tagModel, null);
                }

            }

            if (null != profileModel.getInterests() && profileModel.getInterests().size() > 0) {

                for (InterestModel interestModel : profileModel.getInterests()) {
                    interestModel.setProfileId(profileModel.getId());
                    interestModel.setSelected(true);
                    localIntererest.create(interestModel, null);
                }

            }

            if(null != profileModel.getCategories() && profileModel.getCategories().size() > 0){
                for (CategoryModel categoryModel: profileModel.getCategories()) {
                    categoryModel.setProfileId(profileModel.getId());
                    categoryModel.setSelected(true);
                    localCategory.create(categoryModel);
                }
            }

            if (null != profileModel.getRecipients() && profileModel.getRecipients().size() > 0) {
                localRecipient.create(profileModel.getRecipients().get(0), null);
                Log.d(TAG, "RecipientCreated");
            }
        }else{
            profileModel.setId(model.getId());
            profileModel.setMine(true);
            updateSavedProfile(profileModel);
        }
    }

    @Override
    public void updateProfile(ProfileModel profileModel, final AsyncResult<ProfileModel> result) {
        profileRemote.update("my", profileModel, new AsyncResult<ProfileModel>() {
            @Override
            public void success(ProfileModel profileModel) {
                profileModel.setMine(true);
                ProfileModel createModel = _local.getByServerId(profileModel.getServerId());
                if (createModel == null) {
                    saveProfileToDatabase(profileModel);
                } else {
                    profileModel.setId(createModel.getId());
                    updateSavedProfile(profileModel);
                }

                result.success(profileModel);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public void updateProfileRemote(ProfileModel profileModel, final AsyncResult<ProfileModel> result) {
        profileRemote.update("my", profileModel, new AsyncResult<ProfileModel>() {
            @Override
            public void success(ProfileModel profileModel) {

                result.success(profileModel);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public void updateInterest(ProfileModel profileModel, final AsyncResult<ProfileModel> result) {
        profileRemote.update("my", profileModel, new AsyncResult<ProfileModel>() {
            @Override
            public void success(ProfileModel profileModel) {
                result.success(profileModel);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public void updateSavedProfile(ProfileModel profileModel) {
        update(profileModel, Constants.BROADCAST_MY_PROFILE_VIEW);
        if(null != profileModel.getTags() && profileModel.getTags().size()>0){

            localTag.removeAll();

            for (TagModel tagModel:profileModel.getTags()) {
                tagModel.setProfileId(profileModel.getId());
                tagModel.setSelected(true);
                tagModel.setId(localTag.create(tagModel,null).getId());
            }
        }
        if(null != profileModel.getInterests() && profileModel.getInterests().size()>0){

            localIntererest.removeAll();

            for (InterestModel interestModel:profileModel.getInterests()) {
                interestModel.setProfileId(profileModel.getId());
                interestModel.setSelected(true);
                interestModel.setId(localIntererest.create(interestModel,null).getId());
            }
        }

        if(null != profileModel.getCategories() && profileModel.getCategories().size() > 0){
            localCategory.removeAll();

            for (CategoryModel categoryModel: profileModel.getCategories()) {
                categoryModel.setProfileId(profileModel.getId());
                categoryModel.setSelected(true);
                categoryModel.setId(localCategory.create(categoryModel).getId());
            }
        }

        if(null != profileModel.getRecipients() && profileModel.getRecipients().size() > 0){
            RecipientModel recipientModel = profileModel.getRecipients().get(0);
            RecipientModel model = localRecipient.getByServerId(recipientModel.getServerId());
            if(null == model) {
                localRecipient.create(recipientModel, null);
                Log.d(TAG,"RecipientCreated/upd");

            }else{
                recipientModel.setId(model.getId());
                localRecipient.update(model.getId(),recipientModel);
                Log.d(TAG,"RecipientUpdated");
            }
        }
    }

    @Override
    public void discoverProfiles(PageInput input, AsyncResult<Page<ProfileModel>> result) {
        profileRemote.page(input, result);
    }

    @Override
    public void discoverProfilesForFilter(String query, AsyncResult<Page<ProfileModel>> result) {
        profileRemote.page(null, query, result);
    }

    @Override
    public void sendInvitation(InviteModel model, AsyncResult<InviteModel> result) {
        profileRemote.sendInvitation(model, result);

    }

    @Override
    public void getProfile(String profileId, AsyncResult<ProfileModel> result) {
        profileRemote.get(profileId, result);

    }

    @Override
    public ProfileModel getMyProfile(final AsyncResult<ProfileModel> result) {
        PageQuery query = new PageQuery();
        query.add(ProfileModel.KEY_MINE, true);
//        fetchMyProfile(new PageInput());
        return get(query);
    }

    @Override
    public ProfileModel getUpdatedProfiles(AsyncResult<ProfileModel> result) {
        PageInput input = new PageInput();
        Profile model = new Profile();
        input.query.add("serverId", model.getServerId());
//        fetchMyProfile(input);
        return get(input.query);
    }

    @Override
    public void fetchMyProfile(PageInput pageInput) {
        profileRemote.get("my", new AsyncResult<ProfileModel>() {
            @Override
            public void success(ProfileModel profileModel) {
                profileModel.isMine = true;

                ProfileModel model = _local.getByServerId(profileModel.getServerId());

                if (null == model) {
                    Log.d(TAG, "SavingMyProfile");
                    saveProfileToDatabase(profileModel);
                } else {
                    Log.d(TAG, "updateMyProfile");
                    profileModel.setId(model.getId());
                    updateSavedProfile(profileModel);
                }
            }

            @Override
            public void error(String error) {

            }
        });
    }

    @Override
    public List<ProfileModel> getMyConnections(AsyncResult<Page<ProfileModel>> result) {
        return null;
    }

    @Override
    public void clearMyProfile() {
        _local.removeAll();
    }


}
