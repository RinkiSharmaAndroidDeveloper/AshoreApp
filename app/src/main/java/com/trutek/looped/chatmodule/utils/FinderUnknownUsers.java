package com.trutek.looped.chatmodule.utils;

import android.content.Context;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.services.IChatUserService;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.helpers.RestHelper;
import com.trutek.looped.data.contracts.models.UserModel;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.contracts.PageQuery;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FinderUnknownUsers {

    private Context context;
    private QBChatDialog dialog;
    private List<QBChatDialog> dialogsList;
    private Set<Integer> loadIdsSet;
    private QBUser currentUser;
    private RestHelper restHelper;
    private DataManager dataManager;

    public FinderUnknownUsers(Context context, QBUser currentUser, List<QBChatDialog> dialogsList, DataManager dataManager) {
        this.dataManager = dataManager;

        init(context, currentUser);
        this.dialogsList = dialogsList;
    }

    public FinderUnknownUsers(Context context, QBUser currentUser, QBChatDialog dialog) {
        init(context, currentUser);
        this.dialog = dialog;
    }

    private void init(Context context, QBUser currentUser) {
        this.context = context;
        this.currentUser = currentUser;
        loadIdsSet = new HashSet<Integer>();
        restHelper = new RestHelper(context);
    }

    public void find() {
        if (dialogsList != null) {
            findUserInDialogsList(dialogsList);
        } else {
            findUserInDialog(dialog);
        }
    }

    private void findUserInDialogsList(List<QBChatDialog> dialogsList) {
        for (QBChatDialog dialog : dialogsList) {
            findUserInDialog(dialog);
        }
        if (!loadIdsSet.isEmpty()) {
            loadUsers();
        }
    }

    private void loadUsers() {
        int oneElement = 1;
        try {
            if (loadIdsSet.size() == oneElement) {
                int userId = loadIdsSet.iterator().next();
                ChatUserModel chatUser = RestHelper.loadUser(userId);
                ChatUserModel model = dataManager.getChatUserRepository().get(new PageQuery().add("userId", userId));
                if (model == null){
                    dataManager.getChatUserRepository().create(chatUser,null);
                } else {
                    dataManager.getChatUserRepository().update(model.getId(), chatUser);
                }

            } else {
                Collection<ChatUserModel> userCollection = restHelper.loadUsers(loadIdsSet);
                if (userCollection != null) {
                    for (ChatUserModel user : userCollection){
                        ChatUserModel model = dataManager.getChatUserRepository().get(new PageQuery().add("userId", user.getUserId()));
                        if (model == null){
                            dataManager.getChatUserRepository().create(user);
                        } else {
                            dataManager.getChatUserRepository().update(model.getId(), user);
                        }
                    }
                }
            }
        } catch (QBResponseException e) {
            ErrorUtils.logError(e);
        }
    }

    private void findUserInDialog(QBChatDialog dialog) {
        List<Integer> occupantsList = dialog.getOccupants();
        for (int occupantId : occupantsList) {
            boolean isUserInBase = dataManager.getChatUserRepository().isExist(occupantId);
            if (!isUserInBase && currentUser.getId() != occupantId) {
                loadIdsSet.add(occupantId);
            }
        }
    }
}