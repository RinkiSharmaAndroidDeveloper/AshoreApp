package com.trutek.looped.chatmodule.data.impl.services;

import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.services.IDialogService;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.services.base.BaseService;

/**
 * Created by msas on 9/15/2016.
 */
public class DialogService extends BaseService<DialogModel> implements IDialogService {

    public DialogService(IRepository<DialogModel> local) {
        super(local);
    }
}
