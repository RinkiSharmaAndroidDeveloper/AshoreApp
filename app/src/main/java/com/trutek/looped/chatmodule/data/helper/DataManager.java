package com.trutek.looped.chatmodule.data.helper;

import android.content.Context;

import com.trutek.looped.chatmodule.data.contracts.models.AttachmentModel;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogNotificationModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.MessageModel;
import com.trutek.looped.chatmodule.data.impl.mappers.AttachmentMapper;
import com.trutek.looped.chatmodule.data.impl.mappers.ChatUserMapper;
import com.trutek.looped.chatmodule.data.impl.mappers.DialogMapper;
import com.trutek.looped.chatmodule.data.impl.mappers.DialogNotificationMapper;
import com.trutek.looped.chatmodule.data.impl.mappers.DialogUsersMapper;
import com.trutek.looped.chatmodule.data.impl.mappers.MessageMapper;
import com.trutek.looped.chatmodule.data.impl.repository.AttachmentRepository;
import com.trutek.looped.chatmodule.data.impl.repository.ChatUserRepository;
import com.trutek.looped.chatmodule.data.impl.repository.DialogNotificationRepository;
import com.trutek.looped.chatmodule.data.impl.repository.DialogRepository;
import com.trutek.looped.chatmodule.data.impl.repository.DialogUsersRepository;
import com.trutek.looped.chatmodule.data.impl.repository.MessageRepository;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.impl.entities.Activity;
import com.trutek.looped.data.impl.entities.Attachment;
import com.trutek.looped.data.impl.entities.ChatUser;
import com.trutek.looped.data.impl.entities.Community;
import com.trutek.looped.data.impl.entities.Dialog;
import com.trutek.looped.data.impl.entities.DialogNotification;
import com.trutek.looped.data.impl.entities.DialogUsers;
import com.trutek.looped.data.impl.entities.Message;
import com.trutek.looped.data.impl.mappers.ActivityMapper;
import com.trutek.looped.data.impl.mappers.CommunityMapper;
import com.trutek.looped.data.impl.repositories.ActivityRepository;
import com.trutek.looped.data.impl.repositories.CommunityRepository;
import com.trutek.looped.msas.common.contracts.IModelMapper;

public class DataManager {

    private static DataManager instance;
    private DataHelper dataHelper;
    private DialogRepository dialogRepository;
    private MessageRepository messageRepository;
    private AttachmentRepository attachmentRepository;
    private ChatUserRepository chatUserRepository;
    private DialogUsersRepository dialogUsersRepository;
    private DialogNotificationRepository dialogNotificationRepository;
    private ActivityRepository activityRepository;
    private CommunityRepository communityRepository;

    private DataManager(Context context) {
        dataHelper = new DataHelper(context);
    }

    public static void init(Context context) {
        if (null == instance) {
            instance = new DataManager(context);
        }
    }

    public static DataManager getInstance() {
        return instance;
    }

    public DialogRepository getDialogRepository(){
        IModelMapper<Dialog, DialogModel> mapper = new DialogMapper();
        if(dialogRepository == null){
            dialogRepository = new DialogRepository(mapper , dataHelper.getDialogDao());
        }
        return dialogRepository;
    }

    public MessageRepository getMessageRepository(){
        IModelMapper<Message, MessageModel> mapper = new MessageMapper(getDialogRepository(), getDialogUsersRepository(), getAttachmentRepository());
        if(messageRepository == null){
            messageRepository = new MessageRepository(mapper ,dataHelper.getMessageDao());
        }
        return messageRepository;
    }

    public AttachmentRepository getAttachmentRepository(){
        IModelMapper<Attachment, AttachmentModel> mapper = new AttachmentMapper();
        if(attachmentRepository == null){
            attachmentRepository = new AttachmentRepository(mapper ,dataHelper.getAttachmentDao());
        }
        return attachmentRepository;
    }

    public ChatUserRepository getChatUserRepository(){
        IModelMapper<ChatUser, ChatUserModel> mapper = new ChatUserMapper();
        if(chatUserRepository == null){
            chatUserRepository = new ChatUserRepository(mapper ,dataHelper.getChatUserDao());
        }
        return chatUserRepository;
    }

    public DialogUsersRepository getDialogUsersRepository(){
        IModelMapper<DialogUsers, DialogUserModel> mapper = new DialogUsersMapper(getChatUserRepository(), getDialogRepository());
        if(dialogUsersRepository == null){
            dialogUsersRepository = new DialogUsersRepository(mapper ,dataHelper.getDialogUserMapDao());
        }
        return dialogUsersRepository;
    }

    public DialogNotificationRepository getDialogNotificationRepository() {
        IModelMapper<DialogNotification, DialogNotificationModel> mapper = new DialogNotificationMapper();
        if (dialogNotificationRepository == null) {
            dialogNotificationRepository = new DialogNotificationRepository(null,mapper, dataHelper.getDialogNotificationDao());
        }
        return dialogNotificationRepository;
    }

    public ActivityRepository getActivityRepository() {
       /* IModelMapper<Community, CommunityModel> communityMapper = new CommunityMapper();
        IModelMapper<Activity, ActivityModel> activityMapper = new ActivityMapper(communityMapper);
        if (activityRepository == null) {
            activityRepository = new ActivityRepository(null, activityMapper, dataHelper.getActivityDao());
        }
        return activityRepository;*/
        return null;
    }

}
