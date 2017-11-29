package com.trutek.looped.data.impl.entities;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.trutek.looped.data.impl.entities.User;
import com.trutek.looped.data.impl.entities.Location;
import com.trutek.looped.data.impl.entities.Interest;
import com.trutek.looped.data.impl.entities.Category;
import com.trutek.looped.data.impl.entities.Tag;
import com.trutek.looped.data.impl.entities.Notification;
import com.trutek.looped.data.impl.entities.Community;
import com.trutek.looped.data.impl.entities.Activity;
import com.trutek.looped.data.impl.entities.Profile;
import com.trutek.looped.data.impl.entities.Connection;
import com.trutek.looped.data.impl.entities.Recipient;
import com.trutek.looped.data.impl.entities.Disease;
import com.trutek.looped.data.impl.entities.Loop;
import com.trutek.looped.data.impl.entities.Dialog;
import com.trutek.looped.data.impl.entities.Message;
import com.trutek.looped.data.impl.entities.Attachment;
import com.trutek.looped.data.impl.entities.ChatUser;
import com.trutek.looped.data.impl.entities.DialogNotification;
import com.trutek.looped.data.impl.entities.HealthChart;
import com.trutek.looped.data.impl.entities.HealthParam;
import com.trutek.looped.data.impl.entities.HealthParamLog;
import com.trutek.looped.data.impl.entities.Provider;
import com.trutek.looped.data.impl.entities.Comment;
import com.trutek.looped.data.impl.entities.DialogUsers;

import com.trutek.looped.data.impl.entities.UserDao;
import com.trutek.looped.data.impl.entities.LocationDao;
import com.trutek.looped.data.impl.entities.InterestDao;
import com.trutek.looped.data.impl.entities.CategoryDao;
import com.trutek.looped.data.impl.entities.TagDao;
import com.trutek.looped.data.impl.entities.NotificationDao;
import com.trutek.looped.data.impl.entities.CommunityDao;
import com.trutek.looped.data.impl.entities.ActivityDao;
import com.trutek.looped.data.impl.entities.ProfileDao;
import com.trutek.looped.data.impl.entities.ConnectionDao;
import com.trutek.looped.data.impl.entities.RecipientDao;
import com.trutek.looped.data.impl.entities.DiseaseDao;
import com.trutek.looped.data.impl.entities.LoopDao;
import com.trutek.looped.data.impl.entities.DialogDao;
import com.trutek.looped.data.impl.entities.MessageDao;
import com.trutek.looped.data.impl.entities.AttachmentDao;
import com.trutek.looped.data.impl.entities.ChatUserDao;
import com.trutek.looped.data.impl.entities.DialogNotificationDao;
import com.trutek.looped.data.impl.entities.HealthChartDao;
import com.trutek.looped.data.impl.entities.HealthParamDao;
import com.trutek.looped.data.impl.entities.HealthParamLogDao;
import com.trutek.looped.data.impl.entities.ProviderDao;
import com.trutek.looped.data.impl.entities.CommentDao;
import com.trutek.looped.data.impl.entities.DialogUsersDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userDaoConfig;
    private final DaoConfig locationDaoConfig;
    private final DaoConfig interestDaoConfig;
    private final DaoConfig categoryDaoConfig;
    private final DaoConfig tagDaoConfig;
    private final DaoConfig notificationDaoConfig;
    private final DaoConfig communityDaoConfig;
    private final DaoConfig activityDaoConfig;
    private final DaoConfig profileDaoConfig;
    private final DaoConfig connectionDaoConfig;
    private final DaoConfig recipientDaoConfig;
    private final DaoConfig diseaseDaoConfig;
    private final DaoConfig loopDaoConfig;
    private final DaoConfig dialogDaoConfig;
    private final DaoConfig messageDaoConfig;
    private final DaoConfig attachmentDaoConfig;
    private final DaoConfig chatUserDaoConfig;
    private final DaoConfig dialogNotificationDaoConfig;
    private final DaoConfig healthChartDaoConfig;
    private final DaoConfig healthParamDaoConfig;
    private final DaoConfig healthParamLogDaoConfig;
    private final DaoConfig providerDaoConfig;
    private final DaoConfig commentDaoConfig;
    private final DaoConfig dialogUsersDaoConfig;

    private final UserDao userDao;
    private final LocationDao locationDao;
    private final InterestDao interestDao;
    private final CategoryDao categoryDao;
    private final TagDao tagDao;
    private final NotificationDao notificationDao;
    private final CommunityDao communityDao;
    private final ActivityDao activityDao;
    private final ProfileDao profileDao;
    private final ConnectionDao connectionDao;
    private final RecipientDao recipientDao;
    private final DiseaseDao diseaseDao;
    private final LoopDao loopDao;
    private final DialogDao dialogDao;
    private final MessageDao messageDao;
    private final AttachmentDao attachmentDao;
    private final ChatUserDao chatUserDao;
    private final DialogNotificationDao dialogNotificationDao;
    private final HealthChartDao healthChartDao;
    private final HealthParamDao healthParamDao;
    private final HealthParamLogDao healthParamLogDao;
    private final ProviderDao providerDao;
    private final CommentDao commentDao;
    private final DialogUsersDao dialogUsersDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        locationDaoConfig = daoConfigMap.get(LocationDao.class).clone();
        locationDaoConfig.initIdentityScope(type);

        interestDaoConfig = daoConfigMap.get(InterestDao.class).clone();
        interestDaoConfig.initIdentityScope(type);

        categoryDaoConfig = daoConfigMap.get(CategoryDao.class).clone();
        categoryDaoConfig.initIdentityScope(type);

        tagDaoConfig = daoConfigMap.get(TagDao.class).clone();
        tagDaoConfig.initIdentityScope(type);

        notificationDaoConfig = daoConfigMap.get(NotificationDao.class).clone();
        notificationDaoConfig.initIdentityScope(type);

        communityDaoConfig = daoConfigMap.get(CommunityDao.class).clone();
        communityDaoConfig.initIdentityScope(type);

        activityDaoConfig = daoConfigMap.get(ActivityDao.class).clone();
        activityDaoConfig.initIdentityScope(type);

        profileDaoConfig = daoConfigMap.get(ProfileDao.class).clone();
        profileDaoConfig.initIdentityScope(type);

        connectionDaoConfig = daoConfigMap.get(ConnectionDao.class).clone();
        connectionDaoConfig.initIdentityScope(type);

        recipientDaoConfig = daoConfigMap.get(RecipientDao.class).clone();
        recipientDaoConfig.initIdentityScope(type);

        diseaseDaoConfig = daoConfigMap.get(DiseaseDao.class).clone();
        diseaseDaoConfig.initIdentityScope(type);

        loopDaoConfig = daoConfigMap.get(LoopDao.class).clone();
        loopDaoConfig.initIdentityScope(type);

        dialogDaoConfig = daoConfigMap.get(DialogDao.class).clone();
        dialogDaoConfig.initIdentityScope(type);

        messageDaoConfig = daoConfigMap.get(MessageDao.class).clone();
        messageDaoConfig.initIdentityScope(type);

        attachmentDaoConfig = daoConfigMap.get(AttachmentDao.class).clone();
        attachmentDaoConfig.initIdentityScope(type);

        chatUserDaoConfig = daoConfigMap.get(ChatUserDao.class).clone();
        chatUserDaoConfig.initIdentityScope(type);

        dialogNotificationDaoConfig = daoConfigMap.get(DialogNotificationDao.class).clone();
        dialogNotificationDaoConfig.initIdentityScope(type);

        healthChartDaoConfig = daoConfigMap.get(HealthChartDao.class).clone();
        healthChartDaoConfig.initIdentityScope(type);

        healthParamDaoConfig = daoConfigMap.get(HealthParamDao.class).clone();
        healthParamDaoConfig.initIdentityScope(type);

        healthParamLogDaoConfig = daoConfigMap.get(HealthParamLogDao.class).clone();
        healthParamLogDaoConfig.initIdentityScope(type);

        providerDaoConfig = daoConfigMap.get(ProviderDao.class).clone();
        providerDaoConfig.initIdentityScope(type);

        commentDaoConfig = daoConfigMap.get(CommentDao.class).clone();
        commentDaoConfig.initIdentityScope(type);

        dialogUsersDaoConfig = daoConfigMap.get(DialogUsersDao.class).clone();
        dialogUsersDaoConfig.initIdentityScope(type);

        userDao = new UserDao(userDaoConfig, this);
        locationDao = new LocationDao(locationDaoConfig, this);
        interestDao = new InterestDao(interestDaoConfig, this);
        categoryDao = new CategoryDao(categoryDaoConfig, this);
        tagDao = new TagDao(tagDaoConfig, this);
        notificationDao = new NotificationDao(notificationDaoConfig, this);
        communityDao = new CommunityDao(communityDaoConfig, this);
        activityDao = new ActivityDao(activityDaoConfig, this);
        profileDao = new ProfileDao(profileDaoConfig, this);
        connectionDao = new ConnectionDao(connectionDaoConfig, this);
        recipientDao = new RecipientDao(recipientDaoConfig, this);
        diseaseDao = new DiseaseDao(diseaseDaoConfig, this);
        loopDao = new LoopDao(loopDaoConfig, this);
        dialogDao = new DialogDao(dialogDaoConfig, this);
        messageDao = new MessageDao(messageDaoConfig, this);
        attachmentDao = new AttachmentDao(attachmentDaoConfig, this);
        chatUserDao = new ChatUserDao(chatUserDaoConfig, this);
        dialogNotificationDao = new DialogNotificationDao(dialogNotificationDaoConfig, this);
        healthChartDao = new HealthChartDao(healthChartDaoConfig, this);
        healthParamDao = new HealthParamDao(healthParamDaoConfig, this);
        healthParamLogDao = new HealthParamLogDao(healthParamLogDaoConfig, this);
        providerDao = new ProviderDao(providerDaoConfig, this);
        commentDao = new CommentDao(commentDaoConfig, this);
        dialogUsersDao = new DialogUsersDao(dialogUsersDaoConfig, this);

        registerDao(User.class, userDao);
        registerDao(Location.class, locationDao);
        registerDao(Interest.class, interestDao);
        registerDao(Category.class, categoryDao);
        registerDao(Tag.class, tagDao);
        registerDao(Notification.class, notificationDao);
        registerDao(Community.class, communityDao);
        registerDao(Activity.class, activityDao);
        registerDao(Profile.class, profileDao);
        registerDao(Connection.class, connectionDao);
        registerDao(Recipient.class, recipientDao);
        registerDao(Disease.class, diseaseDao);
        registerDao(Loop.class, loopDao);
        registerDao(Dialog.class, dialogDao);
        registerDao(Message.class, messageDao);
        registerDao(Attachment.class, attachmentDao);
        registerDao(ChatUser.class, chatUserDao);
        registerDao(DialogNotification.class, dialogNotificationDao);
        registerDao(HealthChart.class, healthChartDao);
        registerDao(HealthParam.class, healthParamDao);
        registerDao(HealthParamLog.class, healthParamLogDao);
        registerDao(Provider.class, providerDao);
        registerDao(Comment.class, commentDao);
        registerDao(DialogUsers.class, dialogUsersDao);
    }
    
    public void clear() {
        userDaoConfig.getIdentityScope().clear();
        locationDaoConfig.getIdentityScope().clear();
        interestDaoConfig.getIdentityScope().clear();
        categoryDaoConfig.getIdentityScope().clear();
        tagDaoConfig.getIdentityScope().clear();
        notificationDaoConfig.getIdentityScope().clear();
        communityDaoConfig.getIdentityScope().clear();
        activityDaoConfig.getIdentityScope().clear();
        profileDaoConfig.getIdentityScope().clear();
        connectionDaoConfig.getIdentityScope().clear();
        recipientDaoConfig.getIdentityScope().clear();
        diseaseDaoConfig.getIdentityScope().clear();
        loopDaoConfig.getIdentityScope().clear();
        dialogDaoConfig.getIdentityScope().clear();
        messageDaoConfig.getIdentityScope().clear();
        attachmentDaoConfig.getIdentityScope().clear();
        chatUserDaoConfig.getIdentityScope().clear();
        dialogNotificationDaoConfig.getIdentityScope().clear();
        healthChartDaoConfig.getIdentityScope().clear();
        healthParamDaoConfig.getIdentityScope().clear();
        healthParamLogDaoConfig.getIdentityScope().clear();
        providerDaoConfig.getIdentityScope().clear();
        commentDaoConfig.getIdentityScope().clear();
        dialogUsersDaoConfig.getIdentityScope().clear();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public LocationDao getLocationDao() {
        return locationDao;
    }

    public InterestDao getInterestDao() {
        return interestDao;
    }

    public CategoryDao getCategoryDao() {
        return categoryDao;
    }

    public TagDao getTagDao() {
        return tagDao;
    }

    public NotificationDao getNotificationDao() {
        return notificationDao;
    }

    public CommunityDao getCommunityDao() {
        return communityDao;
    }

    public ActivityDao getActivityDao() {
        return activityDao;
    }

    public ProfileDao getProfileDao() {
        return profileDao;
    }

    public ConnectionDao getConnectionDao() {
        return connectionDao;
    }

    public RecipientDao getRecipientDao() {
        return recipientDao;
    }

    public DiseaseDao getDiseaseDao() {
        return diseaseDao;
    }

    public LoopDao getLoopDao() {
        return loopDao;
    }

    public DialogDao getDialogDao() {
        return dialogDao;
    }

    public MessageDao getMessageDao() {
        return messageDao;
    }

    public AttachmentDao getAttachmentDao() {
        return attachmentDao;
    }

    public ChatUserDao getChatUserDao() {
        return chatUserDao;
    }

    public DialogNotificationDao getDialogNotificationDao() {
        return dialogNotificationDao;
    }

    public HealthChartDao getHealthChartDao() {
        return healthChartDao;
    }

    public HealthParamDao getHealthParamDao() {
        return healthParamDao;
    }

    public HealthParamLogDao getHealthParamLogDao() {
        return healthParamLogDao;
    }

    public ProviderDao getProviderDao() {
        return providerDao;
    }

    public CommentDao getCommentDao() {
        return commentDao;
    }

    public DialogUsersDao getDialogUsersDao() {
        return dialogUsersDao;
    }

}
