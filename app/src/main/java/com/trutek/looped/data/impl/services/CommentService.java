package com.trutek.looped.data.impl.services;

import com.trutek.looped.data.contracts.apis.ICommentApi;
import com.trutek.looped.data.contracts.models.CommentModel;
import com.trutek.looped.data.contracts.services.ICommentService;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IAsyncRemoteApi;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;

import java.util.ArrayList;
import java.util.List;

public class CommentService  extends BaseService<CommentModel> implements ICommentService {

    private IAsyncRemoteApi<CommentModel> remote;
    List<CommentModel> commentModelList =new ArrayList<>();

    public CommentService(IRepository<CommentModel> local,ICommentApi<CommentModel> remote) {
        super(local);
        this.remote = remote;
    }

    @Override
    public void createComment(CommentModel comment, AsyncResult<CommentModel> result) {
        remote.create(comment, result);
    }

    @Override
    public void createSubComment(CommentModel subCommentModel,String commentId, AsyncResult<CommentModel> result) {
        remote.create(subCommentModel, commentId + "/threads", result);
    }

    @Override
    public void allComments(String activityId, final AsyncResult<Page<CommentModel>> result) {
        PageInput input = new PageInput();
        input.query.add("activityId", activityId);
        remote.page(input, new AsyncResult<Page<CommentModel>>(){

            @Override
            public void success(Page<CommentModel> commentModelPage) {
                result.success(commentModelPage);
                /*for ( CommentModel commentModel : commentModelPage.items) {
                    CommentModel createModel=_local.getByServerId(commentModel.getServerId());
                    if(createModel==null){
                        _local.create(commentModel);
                    }else {
                        _local.update(createModel.getId(),commentModel);
                    }
                }*/
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });

    }

    @Override
    public void allCommunityComments(String communityId, AsyncResult<Page<CommentModel>> result) {
        PageInput input = new PageInput();
        input.query.add("communityId", communityId);
        remote.page(input, result);
    }

    @Override
    public void saveCommentLocal(CommentModel commentModel, AsyncResult<CommentModel> result) {
        CommentModel createModel=_local.getByServerId(commentModel.id);
        if(createModel==null){
            _local.create(commentModel);
        }else {
            _local.update(createModel.getId(),commentModel);
        }
    }

    @Override
    public List<CommentModel> getLocalComment(AsyncResult<Page<CommentModel>> result) {
        PageInput input = new PageInput();
        input.query.add("profileId", "all");
        return search(input).items;

    }

    @Override
    public void getSubComments(CommentModel commentModel, AsyncResult<Page<CommentModel>> result) {
        remote.page(null,commentModel.getServerId() + "/threads",result);
    }

    @Override
    public void deleteComment(CommentModel commentModel, AsyncNotify result) {
        remote.delete(commentModel.getServerId(), result);
    }

    @Override
    public void deleteSubComment(CommentModel commentModel, CommentModel subCommentModel, AsyncNotify result) {
        remote.delete(commentModel.getServerId() + "/threads/"+subCommentModel.getServerId(), result);
    }
}
