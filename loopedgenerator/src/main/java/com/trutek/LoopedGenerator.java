package com.trutek;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class LoopedGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(3, "com.trutek.looped.data.impl.entities");

        Entity user = schema.addEntity("User");
        user.addIdProperty();
        user.addStringProperty("name");
        user.addStringProperty("mobile");

        Entity location = schema.addEntity("Location");
        location.addIdProperty();
        location.addStringProperty("name");
        location.addStringProperty("description");
        location.addStringProperty("location");
        location.addStringProperty("serverId");
        location.addDateProperty("timeStamp");
        location.addIntProperty("syncStatus");

        Entity interest = schema.addEntity("Interest");
        interest.addIdProperty();
        interest.addStringProperty("name");
        interest.addStringProperty("serverId");
        interest.addStringProperty("communityId");
        interest.addDateProperty("timeStamp");
        interest.addStringProperty("syncStatus");

        Entity category = schema.addEntity("Category");
        category.addIdProperty();
        category.addStringProperty("name");
        category.addStringProperty("serverId");
        category.addStringProperty("communityId");
        category.addDateProperty("timeStamp");
        category.addStringProperty("syncStatus");

        Entity tag = schema.addEntity("Tag");
        tag.addIdProperty();
        tag.addStringProperty("name");
        tag.addStringProperty("serverId");
        tag.addStringProperty("communityId");
        tag.addDateProperty("timeStamp");
        tag.addStringProperty("syncStatus");

        Entity notification = schema.addEntity("Notification");
        notification.addIdProperty();
        notification.addStringProperty("subject");
        notification.addDateProperty("date");
        notification.addStringProperty("message");
        notification.addStringProperty("data");
        notification.addStringProperty("serverId");
        notification.addDateProperty("timeStamp");
        notification.addStringProperty("syncStatus");

        Entity community = schema.addEntity("Community");
        community.addIdProperty();
        community.addStringProperty("subject");
        community.addStringProperty("body");
        community.addStringProperty("membersCount");
        community.addIntProperty("friendsCount");
        community.addStringProperty("picUrl");
        community.addStringProperty("picData");
        community.addBooleanProperty("isPrivate");
        community.addBooleanProperty("isMine");
        community.addStringProperty("serverId");
        community.addDateProperty("timeStamp");
        community.addStringProperty("syncStatus");

        Entity activity = schema.addEntity("Activity");
        activity.addIdProperty();
        activity.addStringProperty("subject");
        activity.addStringProperty("body");
        activity.addStringProperty("picUrl");
        activity.addStringProperty("picData");
        activity.addStringProperty("type");
        activity.addDateProperty("dueDate");
        activity.addDateProperty("updated_At");
        activity.addBooleanProperty("isPrivate");
        activity.addBooleanProperty("isMine");
        activity.addStringProperty("serverId");
        activity.addDateProperty("timeStamp");
        activity.addStringProperty("syncStatus");

        Entity profile = schema.addEntity("Profile");
        profile.addIdProperty();
        profile.addStringProperty("name");
        profile.addDateProperty("dateOfBirth");
        profile.addStringProperty("about");
        profile.addStringProperty("interests");
        profile.addLongProperty("jabberId");
        profile.addBooleanProperty("isMine");
        profile.addStringProperty("serverId");
        profile.addDateProperty("timeStamp");
        profile.addIntProperty("syncStatus");
        profile.addIntProperty("age");
        profile.addStringProperty("picUrl");
        profile.addStringProperty("gender");
        profile.addStringProperty("location");
        profile.addStringProperty("locationlat");
        profile.addStringProperty("locationlng");

        Entity connection = schema.addEntity("Connection");
        connection.addIdProperty();
        connection.addStringProperty("name");
        connection.addDateProperty("dateOfBirth");
        connection.addStringProperty("about");
        connection.addStringProperty("interests");
        connection.addIntProperty("jabberId");
        connection.addBooleanProperty("isMine");
        connection.addStringProperty("serverId");
        connection.addDateProperty("timeStamp");
        connection.addIntProperty("syncStatus");
        connection.addStringProperty("picUrl");

        Entity recipient = schema.addEntity("Recipient");
        recipient.addIdProperty();
        recipient.addStringProperty("name");
        recipient.addIntProperty("age");
        recipient.addStringProperty("gender");
        recipient.addStringProperty("picUrl");
        recipient.addLongProperty("jabberId");
        recipient.addStringProperty("serverId");
        recipient.addDateProperty("timeStamp");
        recipient.addStringProperty("syncStatus");

        Entity disease = schema.addEntity("Disease");
        disease.addIdProperty();
        disease.addStringProperty("name");
        Index uniqueDisease = new Index();
        uniqueDisease.addProperty(disease.addStringProperty("serverId").notNull().getProperty());
        uniqueDisease.addProperty(disease.addStringProperty("recipientId").notNull().getProperty());
        uniqueDisease.makeUnique();
        disease.addIndex(uniqueDisease);
        disease.addDateProperty("timeStamp");
        disease.addStringProperty("syncStatus");

        Entity loop = schema.addEntity("Loop");
        loop.addIdProperty();
        loop.addStringProperty("name");
        loop.addStringProperty("status");
        loop.addStringProperty("role");
        loop.addStringProperty("profileId");
        loop.addStringProperty("picUrl");
        Index uniqueLoop = new Index();
        uniqueLoop.addProperty(loop.addStringProperty("serverId").notNull().getProperty());
        uniqueLoop.addProperty(loop.addStringProperty("recipientId").notNull().getProperty());
        uniqueLoop.makeUnique();
        loop.addIndex(uniqueLoop);
        loop.addDateProperty("timeStamp");
        loop.addStringProperty("syncStatus");

        Entity dialog=schema.addEntity("Dialog");
        dialog.addIdProperty();
        dialog.addStringProperty("dialogId").unique();
        dialog.addStringProperty("name");
        dialog.addStringProperty("lastMessage");
        dialog.addLongProperty("lastMessageDateSent");
        dialog.addIntProperty("lastMessageUserId");
        dialog.addIntProperty("userId");
        dialog.addStringProperty("xmppRoomJid");
        dialog.addIntProperty("unreadMessagesCount");
        dialog.addStringProperty("imageUrl");
        dialog.addStringProperty("type");
        dialog.addIntProperty("status");
        dialog.addDateProperty("timeStamp");

        Entity message=schema.addEntity("Message");
        message.addIdProperty();
        message.addStringProperty("messageId").unique();
        message.addStringProperty("dialogId");
        message.addStringProperty("body");
        message.addLongProperty("dateSent");
        message.addIntProperty("recipientId");
        message.addIntProperty("senderId");
        message.addStringProperty("state");
        message.addStringProperty("status");
        message.addDateProperty("timeStamp");
        message.addStringProperty("attachmentId").unique();

        Entity attachment = schema.addEntity("Attachment");
        attachment.addIdProperty();
        attachment.addStringProperty("attachmentId").unique();
        attachment.addStringProperty("type");
        attachment.addStringProperty("name");
        attachment.addDoubleProperty("size");
        attachment.addStringProperty("URL");
        attachment.addStringProperty("additionalInfo");
        attachment.addDateProperty("timeStamp");
        attachment.addIntProperty("syncStatus");

        Entity chatUser = schema.addEntity("ChatUser");
        chatUser.addIdProperty();
        chatUser.addIntProperty("userId").unique();
        chatUser.addStringProperty("name");
        chatUser.addStringProperty("number");
        chatUser.addStringProperty("email");
        chatUser.addDateProperty("lastRequestAt");
        chatUser.addStringProperty("role");
        chatUser.addDateProperty("timeStamp");
        chatUser.addIntProperty("syncStatus");

        Entity dialogNotification = schema.addEntity("DialogNotification");
        dialogNotification.addIdProperty();
        dialogNotification.addStringProperty("notificationId").unique();
        dialogNotification.addStringProperty("dialogId");
        dialogNotification.addStringProperty("state");
        dialogNotification.addStringProperty("body");
        dialogNotification.addLongProperty("createdDate");
        dialogNotification.addStringProperty("type");

        Entity healthChart = schema.addEntity("HealthChart");
        healthChart.addIdProperty();
        healthChart.addStringProperty("serverId");

        Entity healthParam = schema.addEntity("HealthParam");
        healthParam.addIdProperty();
        healthParam.addStringProperty("serverId");
        healthParam.addStringProperty("name");
        healthParam.addStringProperty("unit");

        Entity healthParamLog = schema.addEntity("HealthParamLog");
        healthParamLog.addIdProperty();
        healthParamLog.addStringProperty("serverId");
        healthParamLog.addIntProperty("value");
        healthParamLog.addStringProperty("unit");
        healthParamLog.addDateProperty("createdAt");
        healthParamLog.addLongProperty("healthChartId");

        Property healthParamId = healthChart.addLongProperty("healthParamId").getProperty();
        healthChart.addToOne(healthParam,healthParamId);


        Entity provider = schema.addEntity("Provider");
        provider.addIdProperty();
        provider.addStringProperty("name");
        provider.addStringProperty("number");

        Entity comment = schema.addEntity("Comment");
        comment.addIdProperty();
        comment.addStringProperty("serverId");
        comment.addStringProperty("profileId");
        comment.addStringProperty("communityId");
        comment.addStringProperty("activityId");
        comment.addStringProperty("name");
        comment.addStringProperty("text");
        comment.addStringProperty("picUrl");
        comment.addDateProperty("date");
        comment.addStringProperty("subject");


         /*dialog users map*/
        Entity dialogContactMap = schema.addEntity("DialogUsers");
        dialogContactMap.addIdProperty();
        dialogContactMap.addStringProperty("userStatus").notNull();
        Index indexUnique = new Index();
        indexUnique.addProperty(dialogContactMap.addStringProperty("dialogId").notNull().getProperty());
        indexUnique.addProperty(dialogContactMap.addIntProperty("userId").notNull().getProperty());
        indexUnique.makeUnique();
        dialogContactMap.addIndex(indexUnique);

        Property locationId = connection.addLongProperty("locationId").getProperty();
        location.addToMany(connection, locationId);
        connection.addToOne(location, locationId);

        Property recipientId = provider.addLongProperty("recipientId").getProperty();
        recipient.addToMany(provider,recipientId);
        provider.addToOne(provider,recipientId);

        Property profileId = interest.addLongProperty("profileId").getProperty();
        interest.addToOne(profile,profileId);
        profile.addToMany(interest,profileId);

        Property categoryId = category.addLongProperty("profileId").getProperty();
        category.addToOne(profile,categoryId);
        profile.addToMany(category,categoryId);

        Property tagId = tag.addLongProperty("profileId").getProperty();
        tag.addToOne(profile,tagId);
        profile.addToMany(tag,tagId);

        Property communityId = activity.addLongProperty("communityId").getProperty();
        community.addToMany(activity,communityId);
        activity.addToOne(community,communityId);


        new DaoGenerator().generateAll(schema, "../app/src/main/java");

    }
}
