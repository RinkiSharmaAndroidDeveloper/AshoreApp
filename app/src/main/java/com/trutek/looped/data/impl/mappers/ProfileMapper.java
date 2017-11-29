package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.data.contracts.models.CategoryModel;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.LocationModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.impl.entities.Profile;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.ArrayList;

/**
 * Created by msas on 9/6/2016.
 */
public class ProfileMapper implements IModelMapper<Profile, ProfileModel> {

    IRepository<InterestModel> mInterestModelRepository;
    IRepository<LocationModel> mLocationModelRepository;
    IRepository<TagModel> mTagModelRepository;
    private IRepository<CategoryModel> mCategoryRepository;

    public ProfileMapper(IRepository<InterestModel> interestModelRepository
            , IRepository<LocationModel> locationModelRepository
            , IRepository<TagModel> tagModelRepository
            , IRepository<CategoryModel> categoryRepository) {
        mInterestModelRepository = interestModelRepository;
        mLocationModelRepository = locationModelRepository;
        mTagModelRepository = tagModelRepository;

        mCategoryRepository = categoryRepository;
    }

    @Override
    public ProfileModel Map(Profile profile)
    {
        ProfileModel profileModel=new ProfileModel();
        profileModel.setId(profile.getId());
        profileModel.name=profile.getName();
        profileModel.isMine=profile.getIsMine();
        profileModel.setServerId(profile.getServerId());
        profile.getInterestList();
        profileModel.setInterests(mInterestModelRepository.page(new PageInput(ProfileModel.KEY_PROFILE_ID,profileModel.getId())).items);
        profile.getTagList();
        profileModel.setTags(mTagModelRepository.page(new PageInput(ProfileModel.KEY_PROFILE_ID,profileModel.getId())).items);
       if(null !=profile.getLocation()){
           profileModel.location = new LocationModel();
           profileModel.location.setName(profile.getLocation());
            profileModel.location.coordinates.add(profile.getLocationlng());
            profileModel.location.coordinates.add(profile.getLocationlat());
        }
        profileModel.setAbout(profile.getAbout());
        profileModel.dateOfBirth=profile.getDateOfBirth();
        profileModel.setPicUrl(profile.getPicUrl());
        profileModel.setAge(profile.getAge());
        profileModel.setGender(profile.getGender());

        profileModel.setCategories(mCategoryRepository.page(new PageInput(ProfileModel.KEY_PROFILE_ID, profileModel.getId())).items);

        return profileModel;
    }
}
