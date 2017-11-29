package com.trutek.looped.ui.communityDashboard.myConnections;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.ContactModel;
import com.trutek.looped.data.contracts.models.InviteModel;
import com.trutek.looped.data.contracts.models.MemberModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.data.impl.entities.Profile;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.utils.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class SelectConnectionActivity extends BaseAppCompatActivity implements View.OnClickListener{

    @Inject
    IConnectionService connectionService;

    private ArrayList<ConnectionModel> connections;
    private ArrayList<String> selectedContacts;

    private OnActionListener<ConnectionModel> connectionSelectedActionListeners;
    private OnActionListener<ConnectionModel> connectionUnSelectedActionListeners;

    private SelectConnectionAdapter adapter;
    private int OPEN_FORM;
    TextView doneTextView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_view_connection) RecyclerView recyclerViewConnection;
    ActivityModel activity;
    public static void start(Context context){
        Intent intent = new Intent(context, SelectConnectionActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.header_select_done):
                if(OPEN_FORM == OpenForm.EDIT_ACTIVITY || OPEN_FORM == OpenForm.CREATE_ACTIVITY) {
                    Intent intentBack = new Intent();
                    intentBack.putStringArrayListExtra("selectedMembers",selectedContacts);
                    setResult(RESULT_OK, intentBack);
                    finish();
                }
                break;
        }
    }

    public class OpenForm {
        public static final int DISPLAY_COMMUNITY = 1;
        public static final int EDIT_ACTIVITY = 2;
        public static final int CREATE_ACTIVITY = 3;
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_select_connection;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        activity = (ActivityModel) getIntent().getSerializableExtra("ActivityModel");

        initFields();

        initAdapter();
        getConnections();
        doneTextView.setOnClickListener(this);
    }

    public void initFields(){
        connections = new ArrayList<>();
        selectedContacts = new ArrayList<>();
        doneTextView =(TextView)findViewById(R.id.header_select_done);
        Typeface avenirNextRegular=Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        doneTextView.setTypeface(avenirNextRegular);
        if (getIntent().getIntExtra("OPEN_FROM", 0) == OpenForm.DISPLAY_COMMUNITY) {
            OPEN_FORM = OpenForm.DISPLAY_COMMUNITY;
        }
        if (getIntent().getIntExtra("OPEN_FROM", 0) == OpenForm.EDIT_ACTIVITY) {
            OPEN_FORM = OpenForm.EDIT_ACTIVITY;

        }
        if (getIntent().getIntExtra("OPEN_FROM", 0) == OpenForm.CREATE_ACTIVITY) {
            OPEN_FORM = OpenForm.CREATE_ACTIVITY;
        }

        connectionSelectedActionListeners = new OnActionListener<ConnectionModel>() {
            @Override
            public void notify(ConnectionModel connection) {
                selectedContacts.add(connection.profile.getServerId());
            }
        };

        connectionUnSelectedActionListeners = new OnActionListener<ConnectionModel>() {
            @Override
            public void notify(ConnectionModel connection) {
                selectedContacts.remove(connection.profile.getServerId());
            }
        };
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void getConnections() {
        showProgress();
        connectionService.myConnection(new PageInput(), new AsyncResult<List<ConnectionModel>>() {
            @Override
            public void success(final List<ConnectionModel> connectionModels) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connections.addAll(connectionModels);
                        adapter.notifyDataSetChanged();
                        hideProgress();
                        if(OPEN_FORM == OpenForm.EDIT_ACTIVITY){
                            isConnectionAlreadyMember(connectionModels);
                        }
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(error);
                        hideProgress();
                    }
                });
            }
        });
    }
    public void isConnectionAlreadyMember(List<ConnectionModel> connectionModels){
        connections.clear();
        Boolean isMemberExist = true;
        for (ConnectionModel connectionModel :connectionModels)
        {
            for (MemberModel memberModel : activity.participants) {
                    if (connectionModel.profile.getServerId().equals(memberModel.profile.getServerId())) {
                        isMemberExist = true;
                        break;
                    } else {
                        isMemberExist = false;
                    }
                }
                if (!isMemberExist) {
                    connections.add(connectionModel);
                    isMemberExist = true;
                }

        }
        adapter.notifyDataSetChanged();
        hideProgress();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       // super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                break;
        }
        return false;
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.done,menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    private void initAdapter() {
        adapter = new SelectConnectionAdapter(this, connections, connectionSelectedActionListeners,connectionUnSelectedActionListeners);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewConnection.setLayoutManager(linearLayoutManager);
        recyclerViewConnection.setAdapter(adapter);
    }
}
