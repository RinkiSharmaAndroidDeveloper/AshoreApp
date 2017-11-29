package com.trutek.looped.chatmodule.data.impl.mappers;

import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.data.impl.entities.DialogUsers;
import com.trutek.looped.msas.common.contracts.IModelMapper;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.contracts.PageQuery;


public class DialogUsersMapper implements IModelMapper<DialogUsers, DialogUserModel> {

    private IRepository<ChatUserModel> chatUsers;
    private IRepository<DialogModel> dialogs;

    public DialogUsersMapper(IRepository<ChatUserModel> chatUsers, IRepository<DialogModel> dialogs){

        this.chatUsers = chatUsers;
        this.dialogs = dialogs;
    }
    @Override
    public DialogUserModel Map(DialogUsers dialogUsers) {
        DialogUserModel model = new DialogUserModel();
        model.setId(dialogUsers.getId());
        model.setDialogId(dialogUsers.getDialogId());
        model.setUserId(dialogUsers.getUserId());
        model.setUserStatus(DialogUserModel.Status.valueOf(dialogUsers.getUserStatus()));

        if(dialogUsers.getUserId() != 0){
            model.setChatUser(chatUsers.get(new PageQuery().add("userId", dialogUsers.getUserId())));
        }
        if(dialogUsers.getDialogId() != null){
            model.setDialog(dialogs.getByServerId(dialogUsers.getDialogId()));
        }
        return model;
    }
}
