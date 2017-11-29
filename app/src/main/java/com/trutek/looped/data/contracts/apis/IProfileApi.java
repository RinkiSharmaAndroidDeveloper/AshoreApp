package com.trutek.looped.data.contracts.apis;

import com.trutek.looped.data.contracts.models.ContactModel;
import com.trutek.looped.data.contracts.models.InviteModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IAsyncRemoteApi;
import com.trutek.looped.data.contracts.models.ProfileModel;

public interface IProfileApi<TModel> extends IAsyncRemoteApi<ProfileModel> {

    void sendInvitation(InviteModel model, AsyncResult<InviteModel> result);

}
