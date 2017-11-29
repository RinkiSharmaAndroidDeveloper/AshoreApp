package com.trutek.looped.ui.chats;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.quickblox.content.model.QBFile;
import com.quickblox.core.exception.QBResponseException;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.chat.QBCreatePrivateChatCommand;
import com.trutek.looped.chatmodule.commands.chat.QBDeleteChatCommand;
import com.trutek.looped.chatmodule.commands.chat.QBLeaveGroupDialogCommand;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogNotificationModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.MessageModel;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.data.impl.repository.ChatUserRepository;
import com.trutek.looped.chatmodule.data.impl.repository.DialogRepository;
import com.trutek.looped.chatmodule.data.impl.repository.DialogUsersRepository;
import com.trutek.looped.chatmodule.data.impl.repository.MessageRepository;
import com.trutek.looped.chatmodule.service.QuickBloxService;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.ui.base.BaseChatActivity;
import com.trutek.looped.chatmodule.utils.ChatUserUtils;
import com.trutek.looped.chatmodule.utils.ChatUtils;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.UserModel;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.data.impl.entities.Profile;
import com.trutek.looped.gcm.GCMHelper;
import com.trutek.looped.msas.common.Loaders.BaseLoader;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.chats.adapters.InviteToGroupAdapter;
import com.trutek.looped.ui.chats.adapters.PrivateMessagesAdapter;
import com.trutek.looped.ui.communityDashboard.myConnections.MyConnectionActivity;
import com.trutek.looped.ui.home.HomeActivity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by Rinki on 3/24/2017.
 */
public class InviteToGroupActivity extends BaseChatActivity implements View.OnClickListener,InviteToGroupFragment.OnFragmentInteractionListener{
    ImageView backImage;
    ProfileModel profileModel;
    InviteToGroupFragment inviteFragment;
    private GCMHelper gcmHelper;
    @Override
    protected int getContentResId() {
        return R.layout.activity_invite_to_group;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileModel= (ProfileModel) getIntent().getSerializableExtra("Profile");
        loadInviteToGroupFragment();
        gcmHelper = new GCMHelper(this);
        backImage =(ImageView)findViewById(R.id.invite_to_group_back);
        backImage.setOnClickListener(this);
     }



    @Override
    protected void setupActivityComponent() {
    App.get(this).component().inject(this);
        }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isChatInitializedAndUserLoggedIn()) {
            loginChat();
        }

        checkGCMRegistration();
    }
    private void checkGCMRegistration() {
        if (gcmHelper.checkPlayServices()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
                return;
            }
            gcmHelper.registerInBackground();
        } else {

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            gcmHelper.registerInBackground();
        } else {
            ToastUtils.longToast("Required permissions are not granted");
        }
    }
    private void loadInviteToGroupFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        inviteFragment = InviteToGroupFragment.newInstance(profileModel);
        fragmentTransaction.replace(R.id.frame_layout, inviteFragment);
        fragmentTransaction.commit();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.invite_to_group_back):
                finish();
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
