package com.trutek.looped.chatmodule.data.impl.mappers;

import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.data.impl.entities.ChatUser;
import com.trutek.looped.msas.common.contracts.IModelMapper;

/**
 * Created by msas on 9/15/2016.
 */
public class ChatUserMapper implements IModelMapper<ChatUser, ChatUserModel> {

    @Override
    public ChatUserModel Map(ChatUser chatUser) {
        ChatUserModel model = new ChatUserModel();
        model.setId(chatUser.getId());
        model.setUserId(chatUser.getUserId());
        model.setEmail(chatUser.getEmail());
        model.setName(chatUser.getName());
        model.setNumber(chatUser.getNumber());
        model.setLastRequestAt(chatUser.getLastRequestAt());
        model.setRole(ChatUserModel.Role.valueOf(chatUser.getRole()));

        return model;
    }
}
