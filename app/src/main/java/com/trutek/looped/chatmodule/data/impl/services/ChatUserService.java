package com.trutek.looped.chatmodule.data.impl.services;

import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.services.IChatUserService;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.services.base.BaseService;

/**
 * Created by msas on 9/15/2016.
 */
public class ChatUserService extends BaseService<ChatUserModel> implements IChatUserService {

    public ChatUserService(IRepository<ChatUserModel> local) {
        super(local);
    }
}
