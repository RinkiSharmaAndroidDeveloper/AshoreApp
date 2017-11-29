package com.trutek.looped.data.contracts.services;

import com.trutek.looped.data.contracts.models.NotificationModel;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.ICRUDService;

import java.util.List;

public interface INotificationService extends ICRUDService<NotificationModel> {

    void allNotification(AsyncResult<List<NotificationModel>> result);

    void deleteNotification(String id, AsyncNotify notify);
}
