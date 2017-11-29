package com.trutek.looped.data.impl.services;

import com.trutek.looped.data.contracts.apis.INotificationApi;
import com.trutek.looped.data.contracts.models.NotificationModel;
import com.trutek.looped.data.contracts.services.INotificationService;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.IRepository;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.services.base.BaseService;

import java.util.List;

public class NotificationService extends BaseService<NotificationModel> implements INotificationService{

    private INotificationApi<NotificationModel> remote;

    public NotificationService(IRepository<NotificationModel> local, INotificationApi<NotificationModel> remote) {
        super(local);
        this.remote = remote;
    }

    @Override
    public void allNotification(final AsyncResult<List<NotificationModel>> result) {
        remote.page(new PageInput(), new AsyncResult<Page<NotificationModel>>() {
            @Override
            public void success(Page<NotificationModel> notificationModelPage) {
                result.success(notificationModelPage.items);
            }

            @Override
            public void error(String error) {
                result.error(error);
            }
        });
    }

    @Override
    public void deleteNotification(String id, AsyncNotify notify) {
        remote.delete(id, notify);
    }
}
