package com.trutek.looped.data.impl.mappers;

import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommentModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.impl.entities.Comment;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.PageInput;

/**
 * Created by Rinki on 2/19/2017.
 */
public class CommentMapper implements IModelMapper<Comment, CommentModel> {
    IRepository<ProfileModel> mProfileModelRepository;
    IRepository<CommunityModel> mCommunityModelRepository;
    IRepository<ActivityModel> mActivityModelRepository;


    public CommentMapper(IRepository<ProfileModel> profileModelRepository,IRepository<CommunityModel> communityModelRepository,IRepository<ActivityModel> activityModelRepository) {
        mProfileModelRepository = profileModelRepository;
        mCommunityModelRepository = communityModelRepository;
        mActivityModelRepository= activityModelRepository;
    }

    @Override
    public CommentModel Map(Comment comment) {
        CommentModel commentModel=new CommentModel();
        commentModel.setId(comment.getId());
        commentModel.text=comment.getText();
        commentModel.name=comment.getName();
        commentModel.picUrl=comment.getPicUrl();
        commentModel.date=comment.getDate();
        commentModel.profileId=comment.getProfileId();
        if(comment.getActivityId()!=null) {
            commentModel.activityId = comment.getActivityId();
        }
        if(comment.getCommunityId()!=null) {
            commentModel.communityId=comment.getCommunityId();
        }

        return commentModel;
    }
    }

