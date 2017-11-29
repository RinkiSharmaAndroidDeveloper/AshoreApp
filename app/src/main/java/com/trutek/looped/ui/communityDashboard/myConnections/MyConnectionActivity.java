package com.trutek.looped.ui.communityDashboard.myConnections;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.provider.ContactsContract;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.chat.model.QBChatDialog;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.chat.QBCreatePrivateChatCommand;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.utils.ChatUtils;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.FilterModel;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.data.impl.entities.Connection;
import com.trutek.looped.data.impl.services.NotificationService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.chats.PrivateDialogActivity;
import com.trutek.looped.ui.communityDashboard.discoverPeople.DiscoverPeopleAdapter;
import com.trutek.looped.ui.profile.InterestTagsActivity;
import com.trutek.looped.ui.profile.display.DisplayProfile;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.listeners.EndlessScrollListener;
import com.trutek.looped.utils.listeners.RecyclerItemClickListener;

import com.trutek.looped.ui.communityDashboard.myConnections.MyConnectionAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class MyConnectionActivity extends BaseAppCompatActivity implements View.OnClickListener {

    public static final int FILTER_ACTIVITY = 1;
    public static final int REQUEST_DISPLAY_COMMUNITY = 2;

    static final String TAG = MyConnectionActivity.class.getSimpleName();

    @Inject
    IProfileService _ProfileService;
    @Inject
    IConnectionService _connectionService;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    RecyclerView mRecyclerView_connection, mRecyclerView_discoverPeople;
    TextView textView_discover_title, textView_discover, textView_connection_title, textView_connection, textView_title, textView_inviteFriends;
    ProgressBar progressBar_discover, progressBar_connection;

    DiscoverPeopleAdapter discoverPeopleAdapter;
    EditText editText_search;

    private MyConnectionAdapter connectionAdapter;
    private EndlessScrollListener scrollListenerPeople;
    private List<ConnectionModel> connections;
    ArrayList<ProfileModel> discoverPeoples;
    ArrayList<ProfileModel> filterdiscoverPeoples;
    private PageInput peopleInput;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getBooleanExtra("FromService", false)) {
                Log.d(TAG, "BroadCastForRemoteFetch");
                _connectionService.fetchMyConnections(new PageInput());
            } else {
                Log.d(TAG, "BroadCastForLocalFetch");
                connections.clear();
                loadData();
            }

        }
    };

    private ChatUserModel opponentUser;
    private DataManager dataManager;
    private OnActionListener<ConnectionModel> onActionChatListener;

    private FilterModel filter;
    TextView interest, topics, location;
    private List<ConnectionModel> filterConnections;
    @Inject
    IConnectionService connectionService;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, MyConnectionActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_my_connections;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        init();
        listeners();

        setConnections();

        initDiscoverPeopleAdapter();

        // setDataIntoList();
        initializeConnection();
        setFonts();

//        boolean fromChat = getIntent().getBooleanExtra("fromChat", false);
//        if(fromChat){
//            initListeners();
//        }
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_MY_CONNECTIONS));
        //  getApplicationContext().startService(new Intent(getApplicationContext(),NotificationService.class));

    }

    private void init() {
        filter = new FilterModel();
        dataManager = DataManager.getInstance();

        discoverPeoples = new ArrayList<>();
        filterdiscoverPeoples = new ArrayList<>();

        mRecyclerView_connection = (RecyclerView) findViewById(R.id.my_connections_recyclerView);
        mRecyclerView_discoverPeople = (RecyclerView) findViewById(R.id.my_connection_recyclerView_discoverPeople);
        textView_title = (TextView) findViewById(R.id.text_my_connections);

        textView_discover = (TextView) findViewById(R.id.my_connection_textView_discoverPeople);
        textView_discover_title = (TextView) findViewById(R.id.my_connection_textView_discoverPeople_title);
        textView_inviteFriends = (TextView) findViewById(R.id.layout_recycler_textView_info_second);

        textView_connection = (TextView) findViewById(R.id.my_connection_textView_connection);
        textView_connection_title = (TextView) findViewById(R.id.my_connections_textView_connection_title);

        progressBar_discover = (ProgressBar) findViewById(R.id.my_connection_progress_bar_discover);
        progressBar_connection = (ProgressBar) findViewById(R.id.my_connection_progress_bar);

        editText_search = (EditText) findViewById(R.id.my_connection_editText_search);


        onActionChatListener = new OnActionListener<ConnectionModel>() {
            @Override
            public void notify(ConnectionModel connectionModel) {
               // startChat(connectionModel);
            }
        };
    }


    //    }
//        );
//                })
//                    }
//                        finish();
//                        setResult(RESULT_OK, intent);
//                        intent.putExtra("connectionModel", connection);
//                        Intent intent = new Intent();
//                        ConnectionModel connection = connections.get(position);
//                    public void onItemClick(View view, int position) {
//                    @Override
//                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
//        recyclerView.addOnItemTouchListener(
//    private void initListeners() {
//
    private void listeners() {
        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //discoverPeoples
                if (filterdiscoverPeoples != null) {
                    filterdiscoverPeoples.clear();
                }
                /*if(editable.length() == 0){
                    filterdiscoverPeoples.addAll(discoverPeoples);
                }else {
                    for (ProfileModel profileModel : discoverPeoples) {
                        if (profileModel.getName().toLowerCase().startsWith(editable.toString().toLowerCase())) {
                            filterdiscoverPeoples.add(profileModel);
                        }
                    }
                }*/

                for (ProfileModel profileModel : discoverPeoples) {
                    if (profileModel.getName() != null && profileModel.getName().toLowerCase().startsWith(editable.toString().toLowerCase())) {
                        filterdiscoverPeoples.add(profileModel);
                    }
                }
                if (filterdiscoverPeoples != null) {
                    discoverPeopleAdapter.addFilteredData(filterdiscoverPeoples);
                }
                setEmptyText(mRecyclerView_discoverPeople, textView_discover,textView_inviteFriends,discoverPeopleAdapter.getItemCount() != 0);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);

    }

    private void linkWithProfile(ProfileModel profile) {
        ConnectionModel model = new ConnectionModel();
        model.profile = profile;
        connectionService.linkConnection(model, new AsyncResult<ConnectionModel>() {
            @Override
            public void success(ConnectionModel connectionModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       /* resetConnectionState();
                        linearLayout_outgoing.setVisibility(View.VISIBLE);*/
                        ToastUtils.longToast("Connect Successfully");
                        discoverPeopleAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }


    private void initListeners() {
        mRecyclerView_connection.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
//                       ConnectionModel connection = connections.get(position);
                        Intent intent = new Intent();
//                       intent.putExtra("connectionModel", connection);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
        );
    }

    private void initDiscoverPeopleAdapter() {
        discoverPeopleAdapter = new DiscoverPeopleAdapter(discoverPeoples, asyncResult_people_connect, null, this, filterdiscoverPeoples);
        mRecyclerView_discoverPeople.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView_discoverPeople.setAdapter(discoverPeopleAdapter);
        discoverPeopleAdapter.notifyDataSetChanged();
        setEmptyText(mRecyclerView_discoverPeople, textView_discover, textView_inviteFriends,discoverPeopleAdapter.getItemCount() != 0);
        loadPeoplesToBeDiscovered();
    }

    void setEmptyText(RecyclerView recyclerView, TextView textView_noFound,TextView noConnectionFound, boolean anyRecordFound) {
        if (anyRecordFound) {
            recyclerView.setVisibility(View.VISIBLE);
            textView_noFound.setVisibility(View.INVISIBLE);
            noConnectionFound.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            textView_noFound.setVisibility(View.VISIBLE);
            noConnectionFound.setVisibility(View.VISIBLE);
        }
    }

    private void setFonts() {
        Typeface avenirNextRegular = Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        textView_connection_title.setTypeface(avenirNextRegular);
        textView_connection.setTypeface(avenirNextRegular);
        textView_discover_title.setTypeface(avenirNextRegular);
        textView_discover.setTypeface(avenirNextRegular);
        editText_search.setTypeface(avenirNextRegular);
        textView_title.setTypeface(avenirNextRegular);
    }

    void loadPeoplesToBeDiscovered() {
        showCustomProgressDiscover();
        _ProfileService.discoverProfiles(new PageInput(), new AsyncResult<Page<ProfileModel>>() {
            @Override
            public void success(final Page<ProfileModel> profileModelPage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        discoverPeoples.clear();
                        discoverPeoples.addAll(profileModelPage.items);
                        filterdiscoverPeoples.clear();
                        filterdiscoverPeoples.addAll(profileModelPage.items);
                        discoverPeopleAdapter.notifyDataSetChanged();
                        hideCustomProgressDiscover();
                        setEmptyText(mRecyclerView_discoverPeople, textView_discover,textView_inviteFriends, discoverPeopleAdapter.getItemCount() != 0);
                    }
                });
            }

            @Override
            public void error(String error) {

            }
        });
    }

    OnActionListener<ProfileModel> asyncResult_people_connect = new OnActionListener<ProfileModel>() {
        @Override
        public void notify(ProfileModel profileModel) {
            linkWithProfile(profileModel);
        }
    };

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void setConnections() {
        peopleInput = new PageInput();
        connections = new ArrayList<>();
        filterConnections = new ArrayList<>();
        connectionAdapter = new MyConnectionAdapter(this, connections, onActionChatListener,
                filterConnections, asyncResult_deLinkConnection, asyncResult_openConnection);
        mRecyclerView_connection.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView_connection.setAdapter(connectionAdapter);
    }

    private void initializeConnection() {
        peopleInput.pageNo = 1;
        if (scrollListenerPeople != null) {
            scrollListenerPeople.reset();
        }
        _connectionService.fetchMyConnections(new PageInput());
        loadData();
    }

    private void loadData() {
        showCustomProgress();
        connections.clear();
        filterConnections.clear();
        connections.addAll(_connectionService.getMyConnections(null));
        filterConnections.addAll(connections);
        connectionAdapter.notifyDataSetChanged();
        hideCustomProgress();
        setEmptyText(mRecyclerView_connection, textView_connection,textView_inviteFriends,connectionAdapter.getItemCount() != 0);
    }

    private void initializeConnectionForFilter(String query) {
        peopleInput.pageNo = 1;
        if (scrollListenerPeople != null) {
            scrollListenerPeople.reset();
        }
        connections.clear();
        loadDataForFilter(query);
    }

    private void loadDataForFilter(String query) {
        showCustomProgress();
        _connectionService.myConnectionForFilter(query, new AsyncResult<Page<ConnectionModel>>() {
            @Override
            public void success(Page<ConnectionModel> connectionModels) {
                connections.addAll(connectionModels.items);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideCustomProgress();
                        connectionAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideCustomProgress();
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater=getMenuInflater();
//        menuInflater.inflate(R.menu.filter_icon_layout,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

//            case R.id.filter:
//                showFilterDialog();
//                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showCustomProgress() {
        progressBar_connection.setVisibility(View.VISIBLE);
        mRecyclerView_connection.setVisibility(View.INVISIBLE);
    }

    private void showCustomProgressDiscover() {
        progressBar_discover.setVisibility(View.VISIBLE);
        mRecyclerView_discoverPeople.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_MY_CONNECTIONS));
       // addActions();
    }

    private void hideCustomProgress() {
        progressBar_connection.setVisibility(View.GONE);
        mRecyclerView_connection.setVisibility(View.VISIBLE);
    }

    private void hideCustomProgressDiscover() {
        progressBar_discover.setVisibility(View.GONE);
        mRecyclerView_discoverPeople.setVisibility(View.VISIBLE);
    }

    private void showFilterDialog() {
        SeekBar seekBar;
        final TextView txt_redius1, txt_redius2, txt_miles_0, txt_miles_100;
        Button btn_applyFilters;
        final Dialog filterDialog = new Dialog(MyConnectionActivity.this);
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filterDialog.setCanceledOnTouchOutside(false);
        filterDialog.setContentView(R.layout.layout_filter_dialog);

        txt_redius1 = (TextView) filterDialog.findViewById(R.id.filter_dialog_txt_redius1);
        txt_redius2 = (TextView) filterDialog.findViewById(R.id.filter_dialog_txt_redius2);
        txt_miles_0 = (TextView) filterDialog.findViewById(R.id.filter_dialog_miles0);
        txt_miles_100 = (TextView) filterDialog.findViewById(R.id.filter_dialog_miles100);

        interest = (TextView) filterDialog.findViewById(R.id.filter_dialog_edit_text_interest);
        topics = (TextView) filterDialog.findViewById(R.id.filter_dialog_edit_text_topics);
        location = (TextView) filterDialog.findViewById(R.id.filter_dialog_edit_text_location);

        btn_applyFilters = (Button) filterDialog.findViewById(R.id.filter_dialog_btn);
        seekBar = (SeekBar) filterDialog.findViewById(R.id.filter_dialog_seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int current_progress = seekBar.getProgress();
                if (current_progress != seekBar.getMax())
                    txt_miles_100.setText(Integer.toString(current_progress) + " Miles");
                else
                    txt_miles_100.setText("Everywhere");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        filterDialog.show();

        interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyConnectionActivity.this, InterestTagsActivity.class);
                intent.putExtra("filterModel", filter);
                intent.putExtra("OPEN_FORM", 1);
                startActivityForResult(intent, FILTER_ACTIVITY);
            }
        });

        topics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyConnectionActivity.this, InterestTagsActivity.class);
                intent.putExtra("filterModel", filter);
                intent.putExtra("OPEN_FORM", 1);
                startActivityForResult(intent, FILTER_ACTIVITY);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
            }
        });

        btn_applyFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setInputPage();
                filterDialog.dismiss();
            }
        });
    }

    private void setInputPage() {

        String query = makeQuery();
        initializeConnectionForFilter(query);
    }

    private String makeQuery() {
        String query = "?";

        for (InterestModel interestModel : filter.interests) {
            query = query + "interests=" + interestModel.getServerId() + "&";
        }

        for (TagModel tag : filter.tags) {
            query = query + "tags=" + tag.getServerId() + "&";
        }
        return query;
    }

    private void setDialogData(FilterModel filter) {

        interest.setText(filter.interests.size() + " " + "interests");
        topics.setText(filter.tags.size() + " " + "topics");
    }


    private void addActions() {
        addAction(QuickBloxServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION, new CreatePrivateChatSuccessAction());

        updateBroadcastActionList();
    }

  /*  @Override
    protected void onDestroy() {
        super.onDestroy();

        removeActions();
    }

    private void removeActions() {
        removeAction(QuickBloxServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION);

        updateBroadcastActionList();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILTER_ACTIVITY && resultCode == RESULT_OK) {

            filter = (FilterModel) data.getSerializableExtra("filterModel");

            setDialogData(filter);
        }

        if (requestCode == REQUEST_DISPLAY_COMMUNITY) {
            loadData();
        }

    }

    private void startChat(ConnectionModel connection) {
        opponentUser = new ChatUserModel();
        opponentUser.setUserId(connection.getProfile().getChat().id);

        DialogUserModel dialogOccupant = dataManager.getDialogUsersRepository().get(new PageQuery().add("userId", opponentUser.getUserId()));
        if (dialogOccupant != null && dialogOccupant.getDialog() != null) {
            opponentUser = dataManager.getChatUserRepository().get(new PageQuery().add("userId", opponentUser.getUserId()));
            PrivateDialogActivity.start(this, opponentUser, dialogOccupant.getDialog());
            boolean fromChat = getIntent().getBooleanExtra("fromChat", false);
            if (fromChat) {
                finish();
            }
        } else {
            showProgress();
            QBCreatePrivateChatCommand.start(this, opponentUser);
        }
    }

    protected void startPrivateChat(QBChatDialog qbDialog) {
        opponentUser = dataManager.getChatUserRepository().get(new PageQuery().add("userId", opponentUser.getUserId()));
        PrivateDialogActivity.start(this, opponentUser, ChatUtils.createLocalDialog(qbDialog));
        boolean fromChat = getIntent().getBooleanExtra("fromChat", false);
        if (fromChat)
            finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.layout_recycler_textView_info_second):
                Intent i = new Intent(MyConnectionActivity.this, InviteConnectionFromContacts.class);
                startActivity(i);
                break;
        }
    }

    private class CreatePrivateChatSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) throws Exception {
            hideProgress();
            QBChatDialog qbDialog = (QBChatDialog) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_DIALOG);
            startPrivateChat(qbDialog);
        }
    }

    AsyncResult<Integer> asyncResult_deLinkConnection = new AsyncResult<Integer>() {
        @Override
        public void success(Integer position) {
            showProgress();
            _connectionService.deLinkConnection(filterConnections.get(position), asyncNotify_deLinkConnection);
        }

        @Override
        public void error(String error) {

        }
    };


    AsyncNotify asyncNotify_deLinkConnection = new AsyncNotify() {
        @Override
        public void success() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //TODO delete filter connection also
                    //   loadData();
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
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    AsyncResult<ConnectionModel> asyncResult_openConnection = new AsyncResult<ConnectionModel>() {
        @Override
        public void success(ConnectionModel connectionModel) {
            Intent intent = new Intent(MyConnectionActivity.this, DisplayProfile.class);
            intent.putExtra(Constants.MODEL_CONNECTION, connectionModel);
            startActivityForResult(intent, REQUEST_DISPLAY_COMMUNITY);
        }

        @Override
        public void error(String error) {

        }
    };


}
