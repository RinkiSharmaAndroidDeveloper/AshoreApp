package com.trutek.looped.chatmodule.data.impl.services;

import com.trutek.looped.chatmodule.data.contracts.models.MessageModel;
import com.trutek.looped.chatmodule.data.contracts.services.IMessageService;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.services.base.BaseService;

public class MessageService extends BaseService<MessageModel> implements IMessageService {

    private IRepository<MessageModel> local;

    public MessageService(IRepository<MessageModel> local) {
        super(local);
        this.local = local;
    }

}
