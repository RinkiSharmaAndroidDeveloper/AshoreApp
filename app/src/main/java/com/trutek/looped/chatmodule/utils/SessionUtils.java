package com.trutek.looped.chatmodule.utils;


import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.BaseService;
import com.quickblox.core.exception.BaseServiceException;

import java.util.Date;

public class SessionUtils {


    public static boolean isSessionExistOrNotExpired(long expirationTime) {
        try {
            BaseService baseService = QBAuth.getBaseService();
            String token = baseService.getToken();
            if (token == null) {
                return false;
            }
            Date tokenExpirationDate = baseService.getTokenExpirationDate();
            long tokenLiveOffset = tokenExpirationDate.getTime() - System.currentTimeMillis();
            return tokenLiveOffset > expirationTime;
        } catch (BaseServiceException e) {
            // nothing by default
        }
        return false;
    }

}
