package com.trutek.looped.ui.recipient.healthparameter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.HealthChartLogsModel;
import com.trutek.looped.data.contracts.models.HealthChartModel;
import com.trutek.looped.data.contracts.services.IHealthChartLogService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.utils.DialogUtil;
import com.trutek.looped.msas.common.helpers.DateHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

public class DisplayHealthParamLogActivity extends BaseAppCompatActivity {
    final static String TAG = DisplayHealthParamLogActivity.class.getSimpleName();
    @Inject
    IHealthChartLogService _HealthChartLogService;
    MaterialCalendarView mCalendarView;
    RecyclerView mRecyclerView;
    List<HealthChartLogsModel> healthChartLogsModels;
    DisplayHealthParamLogAdapter mDisplayHealthParamLogAdapter;
    TextView textView_title;
    ImageView imageView_back, imageView_delete;
    int selectedLogIndex = -1;
    HealthChartModel mHealthChartModel;
    FloatingActionButton fab_addLog;
    BroadcastReceiver mBroadCaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getLogList();
        }
    };
    @Override
    protected int getContentResId() {
        return R.layout.activity_display_health_param_log;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setActivityTitle();
        setFonts();
        listener();
        initialiseCalendarView();
        initialiseAdapter();
        fetchListFromServer();
        getLogList();
    }
    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }
    @Override

    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadCaseReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(DisplayHealthParamLogActivity.this).registerReceiver(mBroadCaseReceiver,new IntentFilter(Constants.BROADCAST_HEALTH_CHART_LOG));

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadCaseReceiver);

    }

    private void initViews() {
        mCalendarView = (MaterialCalendarView) findViewById(R.id.dhpl_materialCalendarView);
        mRecyclerView = (RecyclerView) findViewById(R.id.dhpl_recyclerView);
        healthChartLogsModels = new ArrayList<>();
        mHealthChartModel = (HealthChartModel) getIntent().getSerializableExtra(Constants.MODEL_HEALTH_CHART);
        fab_addLog = (FloatingActionButton) findViewById(R.id.display_health_param_log_floatingActionButton);
        textView_title = (TextView) findViewById(R.id.dhpl_textView_title);
        imageView_back = (ImageView) findViewById(R.id.dhpl_imageView_back);
        imageView_delete = (ImageView) findViewById(R.id.dhpl_imageView_delete);
    }

    OnActionListener<HealthChartLogsModel> mHealthParamLogValue = new OnActionListener<HealthChartLogsModel>() {
        @Override
        public void notify(HealthChartLogsModel healthChartLogsModel) {
            try {
                selectedLogIndex = healthChartLogsModels.indexOf(healthChartLogsModel);
                DialogUtil.showLogDialog(DisplayHealthParamLogActivity.this, mHealthChartModel.getHealthParam(), healthChartLogsModel, asyncResult_editLogDialog);
            } catch (NullPointerException ex) {
                Log.e(TAG, "FabOnClick: mHealthChartModel cannot be null");
            }
        }
    };

    private void setActivityTitle() {
        if (null != mHealthChartModel)
            textView_title.setText(mHealthChartModel.getHealthParam().getName());
        else {
            textView_title.setText("");
        }
    }

    private void setFonts() {
        textView_title.setTypeface(avenirNextRegular);
    }

    private void listener() {
        fab_addLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DialogUtil.showLogDialog(DisplayHealthParamLogActivity.this, mHealthChartModel.getHealthParam(), null, asyncResult_showLogDialog);
                } catch (NullPointerException ex) {
                    Log.e(TAG, "FabOnClick: mHealthChartModel cannot be null");
                }
            }
        });
        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imageView_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Delete
            }
        });
    }

    void initialiseCalendarView() {
        Calendar calendar = Calendar.getInstance();
        mCalendarView.setSelectedDate(calendar);
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                fetchListFromServer();
            }
        });
    }

    OnDateSelectedListener dateSelectedListener = new OnDateSelectedListener() {
        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
            fetchListFromServer();
        }
    };

    private void getLogList() {
        healthChartLogsModels.clear();
        healthChartLogsModels.addAll(_HealthChartLogService.getAllLogsLocally(DateHelper.onlyDate(mCalendarView.getSelectedDate().getDate()),DateHelper.getDayAfter(1,mCalendarView.getSelectedDate().getDate()),mHealthChartModel.getId()));
        mDisplayHealthParamLogAdapter.notifyDataSetChanged();
    }

    private void initialiseAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mDisplayHealthParamLogAdapter = new DisplayHealthParamLogAdapter(healthChartLogsModels, mHealthParamLogValue);
//        mDisplayHealthParamLogAdapter = new DisplayHealthParamLogAdapter(healthChartLogsModels);
        mRecyclerView.setAdapter(mDisplayHealthParamLogAdapter);
    }

    private void fetchListFromServer() {
        try {
            _HealthChartLogService.getAllLogs(mCalendarView.getSelectedDate().getDate(),
                    DateHelper.getDayAfter(1,mCalendarView.getSelectedDate().getDate()), mHealthChartModel.getServerId(),Constants.BROADCAST_HEALTH_CHART_LOG);
        }catch (NullPointerException ex){
            Log.e(TAG, "HealthChartModel cannot be null");
        }
    }

    AsyncResult<Page<HealthChartLogsModel>> asyncResult_healthChartLogModel = new AsyncResult<Page<HealthChartLogsModel>>() {
        @Override
        public void success(Page<HealthChartLogsModel> healthChartLogsModelPage) {
            Log.d(TAG, "HealthParamLog: fetch - success");
            healthChartLogsModels.clear();
            healthChartLogsModels.addAll(healthChartLogsModelPage.items);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    mDisplayHealthParamLogAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void error(String error) {
            Log.e(TAG, "HealthParamLog: fetch - fail reason: " + error);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                }
            });
        }
    };
    AsyncResult<Integer> asyncResult_editLogDialog = new AsyncResult<Integer>() {
        @Override
        public void success(Integer reading) {
            HealthChartLogsModel healthChartLogsModel;
            if (selectedLogIndex != -1) {
                healthChartLogsModels.get(selectedLogIndex).setValue(reading);
                healthChartLogsModel = healthChartLogsModels.get(selectedLogIndex);
                _HealthChartLogService.updateHealthChartLog(healthChartLogsModel.getServerId(), healthChartLogsModel, asyncResult_editLogParameter);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDisplayHealthParamLogAdapter.notifyItemChanged(selectedLogIndex);
                    }
                });
            }
            Log.d("DIs", "SeltectedIndex: " + selectedLogIndex);
        }
        @Override
        public void error(String error) {
        }
    };

    AsyncResult<HealthChartLogsModel> asyncResult_editLogParameter = new AsyncResult<HealthChartLogsModel>() {
        @Override
        public void success(HealthChartLogsModel healthChartLogsModel) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDisplayHealthParamLogAdapter.notifyItemChanged(selectedLogIndex);
                }
            });
        }
        @Override
        public void error(String error) {

        }
    };
    AsyncResult<Integer> asyncResult_showLogDialog = new AsyncResult<Integer>() {
        @Override
        public void success(Integer reading) {
            HealthChartLogsModel healthChartLogsModel = new HealthChartLogsModel();
            healthChartLogsModel.setHealthChartId(mHealthChartModel.getServerId());
            healthChartLogsModel.setUnit(mHealthChartModel.getHealthParam().getUnits().get(0));
            healthChartLogsModel.setValue(reading);
            _HealthChartLogService.createHealthChartLog(healthChartLogsModel, Constants.BROADCAST_HEALTH_CHART_LOG);
        }
        @Override
        public void error(String error) {

        }
    };
    AsyncResult<HealthChartLogsModel> asyncResult_createHealthChartLog = new AsyncResult<HealthChartLogsModel>() {
        @Override
        public void success(HealthChartLogsModel healthChartLogsModel) {
            showProgress();
            healthChartLogsModels.add(healthChartLogsModel);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    mDisplayHealthParamLogAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void error(String error) {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            hideProgress();
                        }
                    });}
    };

}
