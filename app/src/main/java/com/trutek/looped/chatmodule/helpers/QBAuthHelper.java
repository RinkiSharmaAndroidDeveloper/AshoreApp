package com.trutek.looped.chatmodule.helpers;

import android.content.Context;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.auth.session.QBSessionParameters;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;

import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.trutek.looped.data.contracts.models.AppSession;

public class QBAuthHelper extends BaseHelper {

    private static final String TAG_ANDROID = "android";

    public QBAuthHelper(Context context) {
        super(context);
    }

    public QBUser login() throws QBResponseException, BaseServiceException {

        QBUser qbUser;
        String userMobile = AppSession.getSession().getUser().getPhone();
        String userPassword = AppSession.getSession().getUser().getQbPassword();

        QBUser inputUser = new QBUser(userMobile, userPassword, null);

       QBAuth.createSession();
        String password = inputUser.getPassword();
        qbUser = QBUsers.signIn(inputUser).perform();

        String token = QBAuth.getBaseService().getToken();
        qbUser.setPassword(password);


//        saveOwnerUser(qbUser);

//        AppSession.startSession(LoginType.EMAIL, qbUser, token);

        return qbUser;
    }

//    private void saveOwnerUser(QBUser qbUser) {
//        User user = UserFriendUtils.createLocalUser(qbUser, User.Role.OWNER);
//        DataManager.getInstance().getUserDataManager().createOrUpdate(user);
//    }

//    public QBUser login(String socialProvider, String accessToken,
//                        String accessTokenSecret) throws QBResponseException, BaseServiceException {
//        QBUser qbUser;
//        QBSession session = QBAuth.createSession();
//        qbUser = QBUsers.signInUsingSocialProvider(socialProvider, accessToken, accessTokenSecret);
//        qbUser.setPassword(session.getToken());
//
//        if (!hasUserCustomData(qbUser)) {
//            qbUser.setOldPassword(session.getToken());
//            qbUser = updateUser(qbUser);
//        }
//
//        CoreSharedHelper.getInstance().saveFBToken(accessToken);
//
//        qbUser.setPassword(session.getToken());
//        String token = QBAuth.getBaseService().getToken();
//
//        saveOwnerUser(qbUser);
//
//        AppSession.startSession(LoginType.FACEBOOK, qbUser, token);
//
//        return qbUser;
//    }

//    public QBUser signup(QBUser inputUser, File file) throws QBResponseException, BaseServiceException {
//        QBUser qbUser;
//        UserCustomData userCustomData = new UserCustomData();
//
//        QBAuth.createSession();
//        String password = inputUser.getPassword();
//        inputUser.setOldPassword(password);
//        inputUser.setCustomData(Utils.customDataToString(userCustomData));
//
//        StringifyArrayList<String> stringifyArrayList = new StringifyArrayList<String>();
//        stringifyArrayList.add(TAG_ANDROID);
//        inputUser.setTags(stringifyArrayList);
//
//        qbUser = QBUsers.signUpSignInTask(inputUser);
//
//        if (file != null) {
//            QBFile qbFile = QBContent.uploadFileTask(file, true, (String) null);
//            userCustomData.setAvatar_url(qbFile.getPublicUrl());
//            inputUser.setCustomData(Utils.customDataToString(userCustomData));
//            qbUser = QBUsers.updateUser(inputUser);
//        }
//
//        qbUser.setCustomDataClass(UserCustomData.class);
//        qbUser.setPassword(password);
//        String token = QBAuth.getBaseService().getToken();
//
//        saveOwnerUser(qbUser);
//
//        AppSession.startSession(LoginType.EMAIL, qbUser, token);
//
//        return qbUser;
//    }

//    public void logout() throws QBResponseException {
//        AppSession activeSession = AppSession.getSession();
//        if (activeSession != null) {
//            activeSession.closeAndClear();
//        }
//        Session.getActiveSession().closeAndClearTokenInformation();
//        QBAuth.deleteSession();
//    }
}