package com.trutek.looped.chatmodule.utils;

import com.quickblox.users.model.QBUser;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.msas.common.models.UserModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by msas on 9/16/2016.
 */
public class ChatUserUtils {

    public static ChatUserModel createLocalUser(QBUser qbUser) {
        return createLocalUser(qbUser, ChatUserModel.Role.SIMPLE_ROLE);
    }

    public static ChatUserModel createLocalUser(QBUser qbUser, ChatUserModel.Role role) {
        ChatUserModel user = new ChatUserModel();
        user.setUserId(qbUser.getId());
        user.setName(qbUser.getFullName());
        user.setEmail(qbUser.getPhone());
        user.setEmail(qbUser.getEmail());

        if (qbUser.getLastRequestAt() != null) {
            user.setLastRequestAt(new Date(DateUtilsCore.getTime(qbUser.getLastRequestAt())));
        }

        user.setRole(role);

        return user;
    }

    public static ChatUserModel createDeletedUser(int userId) {
        ChatUserModel user = new ChatUserModel();
        user.setUserId(userId);
        user.setName(String.valueOf(userId));
        return user;
    }

    public static QBUser createQBUser(UserModel userModel) {
        QBUser user = new QBUser();
        user.setId(userModel.getQbId());
        user.setFullName(userModel.getName());
        user.setEmail(userModel.getPhone());
        user.setPhone(userModel.getPhone());
        return user;
    }

    public static List<ChatUserModel> createUsersList(Collection<QBUser> usersList) {
        List<ChatUserModel> users = new ArrayList<>();
        for (QBUser user : usersList) {
            users.add(createLocalUser(user));
        }
        return users;
    }

}
