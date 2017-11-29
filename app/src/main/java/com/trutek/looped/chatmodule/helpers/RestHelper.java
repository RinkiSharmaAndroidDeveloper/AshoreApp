package com.trutek.looped.chatmodule.helpers;

import android.content.Context;
import android.os.Bundle;

import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.services.IChatUserService;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.utils.ChatUserUtils;
import com.trutek.looped.data.contracts.models.UserModel;
import com.trutek.looped.data.impl.entities.ChatUser;
import com.trutek.looped.msas.common.Utils.Constants;

import java.util.Collection;

/**
 * Created by msas on 9/16/2016.
 */
public class RestHelper extends BaseHelper{

    public RestHelper(Context context) {
        super(context);
    }

    public static ChatUserModel loadUser(int userId) {
        ChatUserModel resultUser;

        try {
            QBUser user = QBUsers.getUser(userId).perform();
            resultUser = ChatUserUtils.createLocalUser(user);
        } catch (QBResponseException e) {
            // user not found
            resultUser = ChatUserUtils.createDeletedUser(userId);
        }

        return resultUser;
    }

    public static ChatUserModel loadAndSaveUser(DataManager dataManager, int userId) {
        ChatUserModel chatUser = null;

        try {
            QBUser user = QBUsers.getUser(userId).perform();

            chatUser = ChatUserUtils.createLocalUser(user);
        } catch (QBResponseException e) {
            // user not found
            chatUser = ChatUserUtils.createDeletedUser(userId);
        }

        dataManager.getChatUserRepository().create(chatUser,"");

        return chatUser;
    }

    public Collection<ChatUserModel> loadUsers(Collection<Integer> usersIdsList) throws QBResponseException {
        QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
        requestBuilder.setPage(Constants.USERS_PAGE_NUM);
        requestBuilder.setPerPage(Constants.USERS_PER_PAGE);
        Collection<QBUser> usersList = QBUsers.getUsersByIDs(usersIdsList, requestBuilder, new Bundle()).perform();
        Collection<ChatUserModel> usersListResult = ChatUserUtils.createUsersList(usersList);
        return usersListResult;
    }
}
