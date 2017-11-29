package com.trutek.looped.data.contracts.services;


import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;

public interface IRecipientService extends ICRUDService<RecipientModel> {

    void createRecipient(RecipientModel recipientModel, String intentAction, AsyncResult<RecipientModel> result);

    void createRecipient(RecipientModel recipientModel, String intentAction);

    void createRecipient(RecipientModel recipientModel, AsyncResult<RecipientModel> result);

    void updateRecipient(RecipientModel recipientModel, AsyncResult<RecipientModel> result);

    void getRecipientAndSave(String id, AsyncResult<RecipientModel> result);

    void getRecipient(String id, AsyncResult<RecipientModel> result);

    void getRecipient(String id, String intentAction);

    void acceptRecipientInvitation(RecipientModel id,  AsyncResult<RecipientModel> result);

    void rejectRecipientInvitation(String id,  AsyncResult<RecipientModel> result);

    RecipientModel getLastRecipientFromLocal();
}
