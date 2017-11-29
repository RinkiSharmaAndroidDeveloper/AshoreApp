package com.trutek.looped.chatmodule.data.impl.services;

import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.chatmodule.data.contracts.services.IDialogUsersService;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;

import java.util.List;

/**
 * Created by msas on 9/15/2016.
 */
public class DialogUsersService extends BaseService<DialogUserModel> implements IDialogUsersService {

    public DialogUsersService(IRepository<DialogUserModel> local) {
        super(local);
    }

    @Override
    public List<DialogUserModel> getActualDialogOccupantsByIds(String dialogId, List<Integer> userIdsList) {
        PageInput input = new PageInput();
        input.query.add("dialogId", dialogId);
        input.query.add("userStatus", DialogUserModel.Status.DELETED.name());
        input.query.add("actualDialogOccupantsByIds", true);
        return _local.page(input).items;
    }
}
