package com.trutek.looped.ui.chats;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.chat.QBAddFriendsToGroupCommand;
import com.trutek.looped.chatmodule.commands.chat.QBCreatePrivateChatCommand;
import com.trutek.looped.chatmodule.commands.chat.QBDeleteChatCommand;
import com.trutek.looped.chatmodule.commands.chat.QBLeaveGroupDialogCommand;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogNotificationModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.data.impl.repository.ChatUserRepository;
import com.trutek.looped.chatmodule.data.impl.repository.DialogRepository;
import com.trutek.looped.chatmodule.data.impl.repository.DialogUsersRepository;
import com.trutek.looped.chatmodule.data.impl.repository.MessageRepository;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.utils.ChatUserUtils;
import com.trutek.looped.chatmodule.utils.ChatUtils;
import com.trutek.looped.chatmodule.utils.UserFriendUtils;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.data.impl.entities.Dialog;
import com.trutek.looped.msas.common.Loaders.BaseLoader;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.models.UserModel;
import com.trutek.looped.ui.chats.adapters.DialogsAdapter;
import com.trutek.looped.ui.chats.adapters.InviteToGroupAdapter;
import com.trutek.looped.ui.communityDashboard.myConnections.MyConnectionActivity;
import com.trutek.looped.ui.home.HomeActivity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.listeners.RecyclerItemClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

/**
 * Created by Rinki on 3/25/2017.
 */
public class InviteToGroupFragment extends BaseDialogsFragment<List<DialogModel>> {
    public static final int RESULT_ADDED_FRIENDS = 9123;
    @Inject
    IConnectionService _connectionService;
    private static final String TAG = InviteToGroupFragment.class.getSimpleName();
    private static final int LOADER_ID = InviteToGroupFragment.class.hashCode();

    private static final int NEW_CHAT = 1;

    private RecyclerView dialogsRecyclerView;

    private InviteToGroupAdapter inviteToGroupAdapter;
    private UserModel chatUser;
    private OnFragmentInteractionListener mListener;
    private Observer commonObserver;
    private DataManager dataManager;
    private ChatUserModel opponentUser;
    private OnActionListener<DialogModel> onLongPress;
    private List<DialogNotificationModel.Type> currentNotificationTypeList;
    private ArrayList<Integer> newFriendIdsList;
    private QBChatDialog qbDialog;
    private List<ConnectionModel> connections;
    private Tracker mTracker;
    private List<Integer> friendIdsList;
    ProfileModel profile;

    public InviteToGroupFragment() {
        // Required empty public constructor
    }

    public static InviteToGroupFragment newInstance(ProfileModel profileModel) {
        InviteToGroupFragment fragment = new InviteToGroupFragment();
        Bundle args = new Bundle();
        fragment.profile = profileModel;
        fragment.setArguments(args);
        return fragment;
    }

    public static void start(Activity activity, QBChatDialog qbDialog) {
        Intent intent = new Intent(activity, AddFriendsToGroupActivity.class);
        intent.putExtra(QuickBloxServiceConsts.EXTRA_DIALOG, qbDialog);
        activity.startActivityForResult(intent, RESULT_ADDED_FRIENDS);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (getArguments() != null) {

        }*/
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       // ((HomeActivity) getActivity()).setActionBarTitle(getString(R.string.drawer_text_chats));
    }

    @Override
    protected void setupActivityComponent() {
        App.get(getActivity()).component().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialogs, container, false);
        setHasOptionsMenu(true);
        addActions();
        dialogsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_dialogs);
        connections = new ArrayList<>();
        connections.addAll(_connectionService.getMyConnections(null));
        initViews(view);
        initListeners();
        initFields();
        initChatsDialogs();

        App application = (App) getActivity().getApplicationContext();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.CHAT_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        return view;
    }

    private void initViews(View view) {

        dialogsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_dialogs);
    }

    private void initListeners() {
        dialogsRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        DialogModel dialog = inviteToGroupAdapter.getItem(position);
                        Boolean isAlreadyMember = true;
                        PageInput input = new PageInput();
                        input.query.add("dialogId", dialog.getDialogId());
                        List<DialogUserModel> dialogOccupantsList = dataManager.getDialogUsersRepository()
                                .page(input).items;
                        for (DialogUserModel dialogUserModel :dialogOccupantsList)
                        {
                            if(profile.chat.getId()==dialogUserModel.chatUser.getUserId()){
                                isAlreadyMember =false;
                                break;
                            }
                        }

                        dialog.setUnreadMessagesCount(0);
                        dataManager.getDialogRepository().update(dialog.getId(), dialog, null);
                        if (dialog.getType() == DialogModel.Type.PRIVATE) {
                            startPrivateChatActivity(dialog);
                        } else {
                            if(isAlreadyMember)
                            {
                                performDone(dialog);
                            }else {
                                Toast.makeText(getActivity(),"Already a member of this group",Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                }));

        onLongPress = new OnActionListener<DialogModel>() {
            @Override
            public void notify(DialogModel dialogModel) {

                if (dialogModel.getType() == DialogModel.Type.PRIVATE)

                    showPrivateDialog(dialogModel);
              //  else
                 //   showGroupDialog(dialogModel);

            }
        };
    }

    private void deleteDialog(DialogModel dialog) {
//        if (Dialog.Type.GROUP.equals(dialog.getType())) {
//            if (groupChatHelper != null) {
//                try {
//                    QBDialog localDialog = ChatUtils.createQBDialogFromLocalDialogWithoutLeaved(dataManager,
//                            dataManager.getDialogDataManager().getByDialogId(dialog.getDialogId()));
//                    List<Integer> occupantsIdsList = new ArrayList<>();
//                    occupantsIdsList.add(qbUser.getId());
//                    groupChatHelper.sendGroupMessageToFriends(
//                            localDialog,
//                            DialogNotification.Type.OCCUPANTS_DIALOG, occupantsIdsList, true);
//                    DbUtils.deleteDialogLocal(dataManager, dialog.getDialogId());
//                } catch (QBResponseException e) {
//                    ErrorUtils.logError(e);
//                }
//            }
//        }
        QBDeleteChatCommand.start(getActivity(), dialog.getDialogId(), dialog.getType());
    }

    /*private void leaveDialog(DialogModel dialog) {
        qbDialog = ChatUtils.createQBDialogFromLocalDialog(dataManager, dialog);

        boolean joined = groupChatHelper != null && groupChatHelper.isDialogJoined(dialog);
        if (isChatInitializedAndUserLoggedIn() && checkNetworkAvailableWithError() && joined) {
            showLeaveGroupDialog(dialog);
        } else {
            ToastUtils.longToast(R.string.dialog_details_service_is_initializing);
        }
    }*/

   /* private void showLeaveGroupDialog(final DialogModel dialogModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                leaveGroup(dialogModel);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Are you sure you want to leave group ?");
        dialog.show();
    }*/

    private void leaveGroup(DialogModel dialogModel) {
        showProgress();
        currentNotificationTypeList.add(DialogNotificationModel.Type.OCCUPANTS_DIALOG);
        newFriendIdsList = new ArrayList<>();
        newFriendIdsList.add(AppSession.getSession().getQbUser().getId());
        sendNotificationToGroup(true);
        QBLeaveGroupDialogCommand.start(getActivity(), dialogModel);
    }

    private void sendNotificationToGroup(boolean leavedFromDialog) {
        for (DialogNotificationModel.Type messagesNotificationType : currentNotificationTypeList) {
            try {
                QBChatDialog localDialog = qbDialog;
                if (qbDialog != null) {
                    localDialog = ChatUtils.createQBDialogFromLocalDialogWithoutLeaved(dataManager,
                            dataManager.getDialogRepository().getByServerId(qbDialog.getDialogId()));
                }
                groupChatHelper.sendGroupMessageToFriends(localDialog, messagesNotificationType,
                        newFriendIdsList, leavedFromDialog, dataManager);
            } catch (QBResponseException e) {
                ErrorUtils.logError(e);
                hideProgress();
            }
        }
        currentNotificationTypeList.clear();
    }

    private void showPrivateDialog(final DialogModel dialogModel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.dlg_private_chat_pick, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        deleteDialog(dialogModel);
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }

   /* private void showGroupDialog(final DialogModel dialogModel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.dlg_group_chat_pick, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        leaveDialog(dialogModel);
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
    }
*/
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataLoader(LOADER_ID);
    }

    private void initFields() {
        dataManager = DataManager.getInstance();
        commonObserver = new CommonObserver();
        chatUser = AppSession.getSession().getUser();

        currentNotificationTypeList = new ArrayList<>();

    }

    private void initChatsDialogs() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        List<DialogModel> dialogsList = Collections.emptyList();
        inviteToGroupAdapter = new InviteToGroupAdapter(getActivity(), dialogsList, connections, onLongPress);
        dialogsRecyclerView.setLayoutManager(layoutManager);
        dialogsRecyclerView.setAdapter(inviteToGroupAdapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        addObservers();
        if (inviteToGroupAdapter != null) {
            inviteToGroupAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeActions();
        deleteObservers();
    }

 @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void addAction(String action, Command command) {
        super.addAction(action, command);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    protected Loader<List<DialogModel>> createDataLoader(int id) {
        return new DialogsListLoader(getActivity(), dataManager);
    }

    @Override
    public void onLoadFinished(Loader<List<DialogModel>> loader, List<DialogModel> dialogs) {
        List<DialogModel> dialogList =new ArrayList<>();
        for (DialogModel dialogModel :dialogs)
        {
            if(dialogModel.type == "GROUP")
            {
                dialogList.add(dialogModel);
            }
        }
        inviteToGroupAdapter.setNewData(dialogList);
        inviteToGroupAdapter.notifyDataSetChanged();

    }
    protected void performDone(DialogModel dialog) {
        if (profile !=null) {
            friendIdsList =new ArrayList<>();
            friendIdsList.add(profile.chat.getId());
            QBChatDialog qbDialog= ChatUtils.createQBDialogFromLocalDialogWithoutLeaved(dataManager,dialog);
            boolean joined = true;//groupChatHelper != null && groupChatHelper.isDialogJoined(qbDialog);
            if (isChatInitializedAndUserLoggedIn() && isNetworkAvailable() && joined) {
                showProgress();
               QBAddFriendsToGroupCommand.start(getActivity().getApplicationContext(), qbDialog.getDialogId(),
                        (ArrayList<Integer>) friendIdsList);

            } else {
                ToastUtils.longToast(R.string.chat_service_is_initializing);
            }
        } else {
            ToastUtils.longToast(R.string.add_friends_to_group_no_friends_for_adding);
        }
    }
    private void addActions() {
        addAction(QuickBloxServiceConsts.ADD_FRIENDS_TO_GROUP_SUCCESS_ACTION, new AddFriendsToGroupSuccessCommand());
        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QuickBloxServiceConsts.ADD_FRIENDS_TO_GROUP_SUCCESS_ACTION);
        updateBroadcastActionList();
    }

    private class AddFriendsToGroupSuccessCommand implements Command {

        @Override
        public void execute(Bundle bundle) {
            hideProgress();
            Intent intent = new Intent();
            intent.putExtra(QuickBloxServiceConsts.EXTRA_FRIENDS, (Serializable) friendIdsList);
            getActivity().setResult(RESULT_ADDED_FRIENDS, intent);

        }
    }
    @Override
    public void onLoaderReset(Loader loader) {

    }

    private void updateDialogsList() {
        onChangedData();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.chats, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }*/

 /*   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.chats:
                intent = new Intent(getActivity(), MyConnectionActivity.class);
                intent.putExtra("fromChat", true);
                startActivityForResult(intent, NEW_CHAT);
                break;

            case R.id.new_group:
                intent = new Intent(getActivity(), CreateGroupDialogActivity.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
*/
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    private void startGroupChatActivity(DialogModel dialog) {
        GroupDialogActivity.start(getActivity(), dialog);
    }

    private void startPrivateChatActivity(DialogModel dialog) {
        PageInput input = new PageInput();
        input.query.add("dialogId", dialog.getServerId());
        List<DialogUserModel> occupantsList = dataManager.getDialogUsersRepository().page(input).items;

        ChatUserModel opponent = ChatUtils.getOpponentFromPrivateDialog(ChatUserUtils.createLocalUser(ChatUserUtils.createQBUser(chatUser)), occupantsList);
        if (!TextUtils.isEmpty(dialog.getServerId())) {
            PrivateDialogActivity.start(getActivity(), opponent, dialog);
        }
    }

    @Override
    protected void startPrivateChat(QBChatDialog qbDialog) {
        PrivateDialogActivity.start(getActivity(), opponentUser, ChatUtils.createLocalDialog(qbDialog));
    }

    private void addObservers() {
        dataManager.getDialogRepository().addObserver(commonObserver);
        dataManager.getMessageRepository().addObserver(commonObserver);
        dataManager.getChatUserRepository().addObserver(commonObserver);
        dataManager.getDialogUsersRepository().addObserver(commonObserver);
    }

    private void deleteObservers() {
        dataManager.getDialogRepository().deleteObserver(commonObserver);
        dataManager.getMessageRepository().deleteObserver(commonObserver);
        dataManager.getChatUserRepository().deleteObserver(commonObserver);
        dataManager.getDialogUsersRepository().deleteObserver(commonObserver);
    }

   /* private void startChat(ConnectionModel connection) {
        opponentUser = new ChatUserModel();
        opponentUser.setUserId(connection.getProfile().getChat().id);

        DialogUserModel dialogOccupant = dataManager.getDialogUsersRepository().get(new PageQuery().add("userId", opponentUser.getUserId()));
        if (dialogOccupant != null && dialogOccupant.getDialog() != null) {
            PrivateDialogActivity.start(getActivity(), opponentUser, dialogOccupant.getDialog());
        } else {
            showProgress();
            QBCreatePrivateChatCommand.start(getActivity(), opponentUser);
        }
    }*/


    private static class DialogsListLoader extends BaseLoader<List<DialogModel>> {

        private DataManager dataManager;

        public DialogsListLoader(Context context, DataManager dataManager) {
            super(context);
            this.dataManager = dataManager;
        }

        @Override
        protected List<DialogModel> getItems() {
            List<DialogModel> dialogModels = dataManager.getDialogRepository().page(new PageInput()).items;

            List<DialogModel> dialogs = new ArrayList<>();

            for (DialogModel dialog : dialogModels) {
                if (dialog.getLastMessageDateSent() != null) {
                    dialogs.add(dialog);
                }
            }

            Collections.sort(dialogs, new Comparator<DialogModel>() {
                @Override
                public int compare(DialogModel lhs, DialogModel rhs) {
                    return lhs.getLastMessageDateSent().compareTo(rhs.getLastMessageDateSent());
                }
            });

            Collections.reverse(dialogs);

            return dialogs;
        }
    }

    private class CommonObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            if (data != null) {
                if (data.equals(DialogRepository.OBSERVE_KEY) || data.equals(MessageRepository.OBSERVE_KEY)
                        || data.equals(ChatUserRepository.OBSERVE_KEY) || data.equals(DialogUsersRepository.OBSERVE_KEY)) {
                    updateDialogsList();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_CHAT && resultCode == HomeActivity.RESULT_OK) {
            ConnectionModel connection = (ConnectionModel) data.getSerializableExtra("connectionModel");
            //startChat(connection);
        }
    }
}

