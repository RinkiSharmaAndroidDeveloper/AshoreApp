package com.trutek.looped.chatmodule.utils;

import com.quickblox.users.model.QBUser;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by msas on 11/8/2016.
 */
public class UserFriendUtils {



    public static ArrayList<Integer> getFriendIdsFromUsersList(List<ConnectionModel> friendList) {
        ArrayList<Integer> friendIdsList = new ArrayList<Integer>();
        for (ConnectionModel friend : friendList) {
            friendIdsList.add(friend.profile.chat.id);
        }
        return friendIdsList;
    }

    public static ChatUserModel createLocalUser(QBUser qbUser) {
        return createLocalUser(qbUser, ChatUserModel.Role.SIMPLE_ROLE);
    }

    public static ChatUserModel createLocalUser(QBUser qbUser, ChatUserModel.Role role) {
        ChatUserModel user = new ChatUserModel();
        user.setUserId(qbUser.getId());
        user.setName(qbUser.getFullName());
        user.setEmail(qbUser.getEmail());
        user.setNumber(qbUser.getPhone());
        user.setLogin(qbUser.getLogin());

        if (qbUser.getLastRequestAt() != null) {
            user.setLastRequestAt(qbUser.getLastRequestAt());
        }

        user.setRole(role);
        return user;
    }

    public static ArrayList<Integer> getFriendIds(List<ConnectionModel> friendList) {
        ArrayList<Integer> friendIdsList = new ArrayList<Integer>();
        for (ConnectionModel friend : friendList) {
            friendIdsList.add(friend.profile.chat.id);
        }
        return friendIdsList;
    }

}
