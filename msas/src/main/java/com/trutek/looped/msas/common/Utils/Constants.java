package com.trutek.looped.msas.common.Utils;

public class Constants {

    //   public static final String URL = "http://10.0.0.3:3009/api";
    //   public static final String URL = "http://looped.m-sas.com:3009/api";
         public static final String URL = "http://looped.applegos.in/api"; //Production BaseURL


    public static final int NOT_INITIALIZED_VALUE = -1;
    public static final String EMPTY_STRING = "";
    public static final int ZERO_INT = 0;
    public static final int ZERO_INT_VALUE = 0;

    public static final int DEFAULT_PACKET_REPLY_TIMEOUT = 15 * 1000;
    public static final int LOGIN_TIMEOUT = 40000;

    public static final int TOKEN_VALID_TIME_IN_MINUTES = 1;
    public static final int CHATS_DIALOGS_PER_PAGE = 20;

    public static final int USERS_PAGE_NUM = 1;
    public static final int USERS_PER_PAGE = 100;
    public static final int DIALOG_MESSAGES_PER_PAGE = 20;
    public static final String INACTIVE = "Inactive";

    public static final String SESSION_DOES_NOT_EXIST = "Required session does not exist";

    public static final String REPO_KEY_RECIPEINT_ID = "recipientId";

    public static final String QUERY_KEY_SERVER_ID = "serverId";
    public static final String QUERY_KEY_OCCUPANT_1_ID = "occupantId1";
    public static final String QUERY_KEY_OCCUPANT_2_ID = "occupantId2";
    public static final String QUERY_KEY_DIALOG_ID = "dialogId";


    /*Fonts*/
    public static final String AvenirNextBold = "fonts/AvenirNextLTPro-Bold.otf";
    public static final String AvenirNextRegular = "fonts/AvenirNextLTPro-Regular.otf";

    public static final String BROADCAST_MY_COMMUNITIES = "com.looped.my.communities";
    public static final String BROADCAST_JOINED_COMMUNITIES = "com.looped.joined.communities";
    public static final String BROADCAST_MY_CONNECTIONS = "com.looped.my.connection";
    public static final String BROADCAST_MY_PROFILE = "com.looped.my.profile";
    public static final String BROADCAST_MY_ACTIVITIES = "com.looped.my.activities";
    public static final String BROADCAST_MY_PLANNER = "com.looped.my.planner";
    public static final String BROADCAST_MY_PROFILE_VIEW = "com.looped.my.planner";
    public static final String BROADCAST_EVENT_CREATED = "com.looped.event.created";
    public static final String BROADCAST_POST_CREATED = "com.looped.post.created";
    public static final String BROADCAST_RECIPIENT = "com.looped.recipient";
    public static final String BROADCAST_HEALTH_CHART = "com.looped.healthChart";
    public static final String BROADCAST_HEALTH_CHART_LOG = "com.looped.healthChartLog";

    public static final String KEY_COMMUNITY_ID = "communityId";

    public static final String MODEL_PROFILE = "profileModel";
    public static final String MODEL_CONNECTION = "connectionModel";
    public static final String MODEL_RECIPIENT = "recipientModel";
    public static final String MODEL_CATEGORY = "categoryModel";
    public static final String MODEL_PROVIDER = "providerModel";
    public static final String MODEL_SCHEDULE = "scheduleModel";
    public static final String LIST_PROVIDER = "providersList";
    public static final String MODEL_HEALTHPARAM = "healthParamModel";
    public static final String MODEL_HEALTH_CHART = "healthChartModel";
    public static final String MODEL_CONTACT = "contactModel";
    public static final String INTENT_KEY_DISEASE = "diseaseList";
    public static final String MODEL_COMMUNITY = "communityModel";
    public static final String MODEL_ACTIVITY = "activityModel";

    public static final String TO_DATE = "toDate";
    public static final String FROM_DATE = "fromDate";
    public static final String ORDER_ASC = "orderASC";
    public static final String ORDER_ASC_NO_LIMIT = "orderASCNoLimit";
    public static final String ORDER_DESC = "orderDSC";
    public static final String ORDER_DESC_NO_LIMIT = "orderDSCNoLimit";
    public static final String GREATER_THEN = "greaterThan";

    public static final String PLANNER_SCREEN = "Planner screen";
    public static final String HOME_SCREEN = "My Dashboard";
    public static final String Activity_SCREEN = "Activity screen";
    public static final String DISCOVERCOMMUNITY_SCREEN = "DiscoverCommunity screen";
    public static final String DISCOVERPEOPLE_SCREEN = "DiscoverPeople screen";
    public static final String COMMUNITYDASHBOARD_SCREEN = "DiscoverCommunity screen";
    public static final String MYCOMMUNITY_JOINED_SCREEN = "MyCommunity  joined screen";
    public static final String MYCOMMUNITY_CREATED_SCREEN = "MyCommunity  created screen";
    public static final String MYCONNECTION_SCREEN = "MyConnections screen";
    public static final String CHAT_SCREEN = "Chat screen";
    public static final String GROUPCHAT_SCREEN = "Group chat screen";
    public static final String PRIVATECHAT_SCREEN = "PrivateDialogActivity screen";

    public static final String INTENT_KEY_POSITION = "position";
    public static final String INTENT_KEY_COMMENTS = "commentsList";
    public static final String INTENT_KEY_SUB_COMMENTS = "subCommentsList";

}
