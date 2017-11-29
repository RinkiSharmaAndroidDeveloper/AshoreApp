package com.trutek.looped.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.NotificationModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.data.contracts.services.INotificationService;
import com.trutek.looped.data.contracts.services.IRecipientService;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.ui.activity.display.discussion.DiscussionActivity;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.profile.display.DisplayProfile;
import com.trutek.looped.ui.recipient.recipient.display.DisplayRecipientActivity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class NotificationActivity extends BaseAppCompatActivity {

    @Inject
    INotificationService notificationService;
    @Inject
    IConnectionService connectionService;
    @Inject
    IRecipientService recipientService;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.txt_header_notification) TextView header;
    @BindView(R.id.notification_recyclerView) RecyclerView recyclerView;

    private NotificationAdapter notificationAdapter;
    private OnActionListener<NotificationModel> onAcceptActionListener;
    private OnActionListener<NotificationModel> onRejectActionListener;
    private OnActionListener<NotificationModel> onActionListener;
    private ArrayList<NotificationModel> notifications;
    @Override
    protected int getContentResId() {
        return R.layout.activity_notification;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initFields();
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void initFields(){
        notifications = new ArrayList<>();

        initListeners();
        initAdapters();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNotifications();
    }

    private void initListeners() {
        onAcceptActionListener = new OnActionListener<NotificationModel>() {
            @Override
            public void notify(NotificationModel notification) {
                acceptRequest(notification);
            }
        };

        onRejectActionListener = new OnActionListener<NotificationModel>() {
            @Override
            public void notify(NotificationModel notification) {
                deleteNotification(notification.getServerId());
            }
        };

        onActionListener = new OnActionListener<NotificationModel>() {
            @Override
            public void notify(NotificationModel notification) {
                resolveActivity(notification);
            }
        };
    }

    private void resolveActivity(NotificationModel notification) {
        Intent intent = null;
        if (notification.data.entity.type.equalsIgnoreCase("profile")){
            ProfileModel profile = new ProfileModel();
            profile.setServerId(notification.data.entity.id);
            profile.status=notification.data.action;
            intent = new Intent(this, DisplayProfile.class);
            intent.putExtra("profileModel", profile);
        } else if (notification.data.entity.type.equalsIgnoreCase("activity")){
            ActivityModel activityModel = new ActivityModel();
            activityModel.setServerId(notification.data.entity.id);
            intent = new Intent(this, DiscussionActivity.class);
            intent.putExtra("activityModel", activityModel);
        } else if (notification.data.entity.type.equalsIgnoreCase("recipient")){
            RecipientModel recipientModel = new RecipientModel();
            recipientModel.setServerId(notification.data.entity.id);
            intent = new Intent(this, DisplayRecipientActivity.class);
            intent.putExtra("requestForRecipient", true);
            intent.putExtra("notificationId", notification.getServerId());
            intent.putExtra("recipient", recipientModel);
        }

        if(intent != null)
            startActivity(intent);
    }

    private void acceptRequest(final NotificationModel notification){
        if(notification.data.entity.type.equalsIgnoreCase("recipient")){
            acceptRecipient(notification);
        } else {
            acceptConnectionRequest(notification);
        }
    }

    private void acceptConnectionRequest(final NotificationModel notification){
        ConnectionModel connection = new ConnectionModel();
        connection.profile = new ProfileModel();
        connection.profile.id = notification.data.entity.id;

        showProgress();
        connectionService.linkConnection(connection, new AsyncResult<ConnectionModel>() {
            @Override
            public void success(ConnectionModel connectionModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        deleteNotification(notification.id);
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }

    private void acceptRecipient(final NotificationModel notification) {
        showProgress();
        recipientService.getRecipient(notification.data.entity.id, new AsyncResult<RecipientModel>() {
            @Override
            public void success(final RecipientModel recipientModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recipientService.acceptRecipientInvitation(recipientModel, new AsyncResult<RecipientModel>() {
                            @Override
                            public void success(RecipientModel recipientModel) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        deleteNotification(notification.id);
                                    }
                                });
                            }

                            @Override
                            public void error(final String error) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgress();
                                        ToastUtils.longToast(error);
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }

    private void deleteNotification(String id){
        notificationService.deleteNotification(id, new AsyncNotify() {
            @Override
            public void success() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getNotifications();

                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }

    private void initAdapters(){
        notificationAdapter = new NotificationAdapter(this, notifications,
                onAcceptActionListener, onRejectActionListener, onActionListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(notificationAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getNotifications(){
        notifications.clear();
        notificationService.allNotification(new AsyncResult<List<NotificationModel>>() {
            @Override
            public void success(final List<NotificationModel> notificationModels) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifications.addAll(notificationModels);

                        Collections.sort(notifications, new Comparator<NotificationModel>() {
                            @Override
                            public int compare(NotificationModel lhs, NotificationModel rhs) {
                                return lhs.date.compareTo(rhs.date);
                            }
                        });

                        Collections.reverse(notifications);
                        notificationAdapter.notifyDataSetChanged();
                        hideProgress();
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }
}
