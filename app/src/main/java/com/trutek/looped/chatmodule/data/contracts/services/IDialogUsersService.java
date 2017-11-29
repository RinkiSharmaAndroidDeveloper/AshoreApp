package com.trutek.looped.chatmodule.data.contracts.services;

import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.msas.common.contracts.ICRUDService;

import java.util.List;

/**
 * Created by msas on 9/15/2016.
 */
public interface IDialogUsersService  extends ICRUDService<DialogUserModel> {

    List<DialogUserModel> getActualDialogOccupantsByIds(String dialogId, List<Integer> userIdsList);
}
