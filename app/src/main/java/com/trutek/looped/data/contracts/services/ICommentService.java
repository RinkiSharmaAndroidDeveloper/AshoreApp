package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.CommentModel;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;

import java.util.List;

public interface ICommentService  extends ICRUDService<CommentModel> {

    void createComment(CommentModel comment, AsyncResult<CommentModel> result);

    void createSubComment(CommentModel subCommentModel,String commentId, AsyncResult<CommentModel> result);

    void allComments(String activityId, AsyncResult<Page<CommentModel>> result);
    void allCommunityComments(String communityId, AsyncResult<Page<CommentModel>> result);
    void saveCommentLocal(CommentModel commentModel, AsyncResult<CommentModel> result);
    List<CommentModel> getLocalComment(AsyncResult<Page<CommentModel>> result);

    void getSubComments(CommentModel commentModel, AsyncResult<Page<CommentModel>> result);

    void deleteComment(CommentModel commentModel , AsyncNotify result);

    void deleteSubComment(CommentModel commentModel, CommentModel subCommentModel, AsyncNotify result);


}
