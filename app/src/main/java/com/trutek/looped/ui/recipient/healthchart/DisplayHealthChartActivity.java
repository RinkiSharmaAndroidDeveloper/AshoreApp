package com.trutek.looped.ui.recipient.healthchart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.HealthChartLogsModel;
import com.trutek.looped.data.contracts.models.HealthChartModel;
import com.trutek.looped.data.contracts.models.HealthParameterModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.services.IHealthChartLogService;
import com.trutek.looped.data.contracts.services.IHealthChartService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.recipient.healthparameter.AddHealthParameterActivity;
import com.trutek.looped.ui.recipient.healthparameter.DisplayHealthParamLogActivity;
import com.trutek.looped.utils.DialogUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DisplayHealthChartActivity extends BaseAppCompatActivity implements View.OnClickListener {

    @Inject
    IHealthChartService _HealthChartService;

    @Inject
    IHealthChartLogService _HealthChartLogService;

    static final String TAG = DisplayHealthChartActivity.class.getSimpleName();

    FloatingActionButton floatingActionButton_add_healthParameter;
    TextView textView_title;
    ImageView imageView_back;

    RecyclerView mRecyclerView;
    RecipientModel mRecipientModel;

    List<HealthChartModel> mHealthChartModels;

    DisplayHealthChartAdapter mDisplayHealthChartAdapter;

    int mIndexOfSelectedParamForLog = -1;

    BroadcastReceiver mLocalBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getAllHealthChartsLocal();
            Log.d(TAG,"BroadCast");
        }
    };

    static final int REQUEST_CODE_ADD_HEALTH_PARAMETER = 1;

    @Override
    protected int getContentResId() {
        return R.layout.activity_display_health_chart;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setFonts();
        listener();
        initialiseAdapter();
        getAllHealthChartsServer();
        getAllHealthChartsLocal();
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void initViews() {
        floatingActionButton_add_healthParameter = (FloatingActionButton) findViewById(R.id.display_health_chart_floatingActionButton);
        textView_title = (TextView) findViewById(R.id.display_health_chart_textView_title);
        imageView_back = (ImageView) findViewById(R.id.display_health_chart_imageView_back);

        mRecyclerView = (RecyclerView) findViewById(R.id.display_health_chart_recyclerView);

        mRecipientModel = (RecipientModel) getIntent().getSerializableExtra(Constants.MODEL_RECIPIENT);
        mHealthChartModels = new ArrayList<>();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mLocalBroadCast,new IntentFilter(Constants.BROADCAST_HEALTH_CHART));
    }

    private void setFonts() {
        textView_title.setTypeface(avenirNextRegular);
    }

    private void listener() {
        imageView_back.setOnClickListener(this);
        floatingActionButton_add_healthParameter.setOnClickListener(this);
    }

    private void initialiseAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDisplayHealthChartAdapter = new DisplayHealthChartAdapter(mHealthChartModels, selectedHealthChart, mCreateHealthChartLogListener);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mDisplayHealthChartAdapter);

    }

    private void getAllHealthChartsServer(){
        _HealthChartService.getAll(mRecipientModel.getServerId(),Constants.BROADCAST_HEALTH_CHART);
    }

    private void getAllHealthChartsLocal() {
        try {
            mHealthChartModels.clear();
            mHealthChartModels.addAll(_HealthChartService.getAllFromLocal());
            mDisplayHealthChartAdapter.modified();
        } catch (NullPointerException ex) {
            Log.e(TAG, "RecipientModel is null");
            hideProgress();
        }
    }

    OnActionListener<HealthChartModel> selectedHealthChart = new OnActionListener<HealthChartModel>() {
        @Override
        public void notify(HealthChartModel healthChartModel) {
            Intent intent = new Intent(DisplayHealthChartActivity.this, DisplayHealthParamLogActivity.class);
            intent.putExtra(Constants.MODEL_HEALTH_CHART, healthChartModel);
            startActivity(intent);
        }
    };

    OnActionListener<HealthChartModel> mCreateHealthChartLogListener = new OnActionListener<HealthChartModel>() {
        @Override
        public void notify(final HealthChartModel healthChartModel) {
            DialogUtil.showLogDialog(DisplayHealthChartActivity.this, healthChartModel.getHealthParam(), null, new AsyncResult<Integer>() {
                @Override
                public void success(Integer reading) {
                    mIndexOfSelectedParamForLog = mHealthChartModels.indexOf(healthChartModel);
                    HealthChartLogsModel healthChartLogsModel = new HealthChartLogsModel();
                    healthChartLogsModel.setUnit(healthChartModel.getHealthParam().getUnits().get(0));
                    healthChartLogsModel.setValue(reading);
                    healthChartLogsModel.setHealthChartId(healthChartModel.getServerId());
                    healthChartLogsModel.setHealthChartLocalId(healthChartModel.getId());
                    createHealthChartLog(healthChartLogsModel);
                }

                @Override
                public void error(String error) {

                }
            });
        }
    };


    @Override
    public void onClick(View view) {
        if (view.getId() == imageView_back.getId()) {
            finish();
        } else if (view.getId() == floatingActionButton_add_healthParameter.getId()) {
            Log.e(TAG, "fabClicked");
            Intent intent = new Intent(DisplayHealthChartActivity.this, AddHealthParameterActivity.class);
            intent.putExtra("lists", getAllHealthParam(mHealthChartModels));
            startActivityForResult(intent, REQUEST_CODE_ADD_HEALTH_PARAMETER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_ADD_HEALTH_PARAMETER && resultCode == RESULT_OK && null != data) {

            HealthParameterModel healthParameterModel = (HealthParameterModel) data.getSerializableExtra(Constants.MODEL_HEALTHPARAM);
            HealthChartModel healthChartModel = new HealthChartModel();
            healthChartModel.setRecipient(mRecipientModel);
            healthChartModel.setHealthParam(healthParameterModel);
            createHealthChart(healthChartModel);
        }

    }

    void createHealthChart(HealthChartModel healthChartModel) {
        Log.d(TAG, String.format("CreateHealthChart: RecipientId: %s HealthParamId: %s",
                healthChartModel.getRecipient().getServerId(), healthChartModel.getHealthParam().getServerId()));

        _HealthChartService.createHealthChart(healthChartModel, Constants.BROADCAST_HEALTH_CHART);
    }

    void createHealthChartLog(HealthChartLogsModel healthChartLogsModel) {
        Log.d(TAG, String.format("CreateHealthChartLog: HealthChartId: %s value: %d unit: %s",
                healthChartLogsModel.getHealthChartId(), healthChartLogsModel.getValue(), healthChartLogsModel.getUnit()));

        _HealthChartLogService.createHealthChartLog(healthChartLogsModel, Constants.BROADCAST_HEALTH_CHART);

    }

    AsyncResult<HealthChartModel> asyncResult_createHealthChart = new AsyncResult<HealthChartModel>() {
        @Override
        public void success(HealthChartModel healthChartModel) {
            Log.d(TAG, "HealthChartCreated: success");
            mHealthChartModels.add(healthChartModel);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    mDisplayHealthChartAdapter.modified();
                }
            });
        }

        @Override
        public void error(String error) {
            if (null != error) {
                Log.d(TAG, "HealthChartCreated: fail. Reason: " + error);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                }
            });
        }
    };

    AsyncResult<HealthChartLogsModel> asyncResult_createHealthChartLog = new AsyncResult<HealthChartLogsModel>() {
        @Override
        public void success(HealthChartLogsModel healthChartLogsModel) {
            Log.d(TAG, "HealthChartLogCreated: success");
            if (mIndexOfSelectedParamForLog == -1) {
                Log.e(TAG, "Invalid index for selected health param logs");
                return;
            }

            List<HealthChartLogsModel> chartLogsModels = mHealthChartModels.get(mIndexOfSelectedParamForLog).getLogs();
            chartLogsModels.add(healthChartLogsModel);
            mHealthChartModels.get(mIndexOfSelectedParamForLog).setLogs(chartLogsModels);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    mDisplayHealthChartAdapter.notifyItemChanged(mIndexOfSelectedParamForLog);
                }
            });
        }

        @Override
        public void error(String error) {
            if (null != error) {
                Log.d(TAG, "HealthChartLogCreated: fail. Reason: " + error);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mLocalBroadCast,new IntentFilter(Constants.BROADCAST_HEALTH_CHART));

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mLocalBroadCast);
    }



    ArrayList<HealthParameterModel> getAllHealthParam(List<HealthChartModel> healthChartModels) {

        ArrayList<HealthParameterModel> healthParameterModels = new ArrayList<>();

        for (HealthChartModel healthChartModel : healthChartModels) {
            healthChartModel.getHealthParam().setSelected(true);
            healthParameterModels.add(healthChartModel.getHealthParam());
        }
        return healthParameterModels;
    }
}
