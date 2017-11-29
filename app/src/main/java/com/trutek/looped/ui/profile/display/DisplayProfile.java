package com.trutek.looped.ui.profile.display;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.quickblox.chat.model.QBChatDialog;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.commands.chat.QBCreatePrivateChatCommand;
import com.trutek.looped.chatmodule.data.contracts.models.ChatUserModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogModel;
import com.trutek.looped.chatmodule.data.contracts.models.DialogUserModel;
import com.trutek.looped.chatmodule.data.helper.DataManager;
import com.trutek.looped.chatmodule.service.QuickBloxServiceConsts;
import com.trutek.looped.chatmodule.utils.ChatUserUtils;
import com.trutek.looped.chatmodule.utils.ChatUtils;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.data.contracts.models.ConnectionModel;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.NotificationModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.ReportBugModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.contracts.services.IConnectionService;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.data.contracts.services.IReportBugService;
import com.trutek.looped.data.impl.entities.ChatUser;
import com.trutek.looped.data.impl.entities.Profile;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.commands.Command;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.models.UserModel;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.chats.InviteToGroupActivity;
import com.trutek.looped.ui.chats.PrivateDialogActivity;
import com.trutek.looped.ui.dialogs.PreviewImageFragment;
import com.trutek.looped.ui.profile.create.adapter.InterestAdapter;
import com.trutek.looped.utils.DialogUtil;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.image.ImageLoaderUtils;
import com.trutek.looped.utils.listeners.EndlessScrollListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class DisplayProfile extends BaseAppCompatActivity implements PreviewImageFragment.OnFragmentInteractionListener, View.OnClickListener {

    private final static String PREVIEW_IMAGE_FRAGMENT = "PreviewImageFragment";

    private static final float BLUR_RADIUS = 20f;
    private static final String TAG = DisplayProfile.class.getSimpleName();
    @Inject
    IProfileService profileService;
    @Inject
    IConnectionService connectionService;
    @Inject
    IReportBugService _ReportBugService;

    //   @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.txt_header)
    TextView header;
    @BindView(R.id.txt_about_me)
    TextView aboutMe;
    @BindView(R.id.txt_gender)
    TextView gender;
    @BindView(R.id.txt_date_of_birth)
    TextView age;
    @BindView(R.id.txt_location)
    TextView location;
    // @BindView(R.id.scroll_view) ScrollView scrollView;
    // @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.image_view_profile)
    MaskedImageView masked_profileImage;
    @BindView(R.id.image_view_profile_blur)
    ImageView blur_image;
    Button cancel, sentRequest, acceptRequest, dismiss, chat, inviteGroup;

    //  @BindView(R.id.edit_profile_title_interested_in) TextView text_title_interested_in;
    //  @BindView(R.id.edit_profile_recycler_view_topics) RecyclerView recyclerViewTopics;

    // @BindView(R.id.edit_profile_title_my_topics) TextView text_title_my_topics;
    @BindView(R.id.edit_profile_recycler_view_interest)
    RecyclerView recyclerViewInterest;


    // private TagAdapter tagAdapter;
    private InterestAdapter interestAdapter;

    // PageInput tagInput;
    PageInput interestsInput;

    private EndlessScrollListener scrollListenerTopic;
    private EndlessScrollListener scrollListenerInterest;

    //  private ArrayList<TagModel> tagList;
    private ArrayList<InterestModel> interestsList;

    private ProfileModel profile;
    ConnectionModel connectionModel;
    private String imageURL;
    NotificationModel notification;
    private ChatUserModel opponentUser;
    private DataManager dataManager;

    LinearLayout linearLayout_incoming, linearLayout_outgoing, linearLayout_connected;
    Button button_connect;

    TextView textView_header, textView_aboutMe, textView_gender, textView_age, textView_location, textView_interest;
    ImageView imageView_back;

    LinearLayout linearLayout_option;

    @Override
    protected int getContentResId() {
        return R.layout.activity_discover_people_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //  setSupportActionBar(toolbar);

/*        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);*/

        dataManager = DataManager.getInstance();
        profile = (ProfileModel) getIntent().getSerializableExtra("profileModel");
        connectionModel = (ConnectionModel) getIntent().getSerializableExtra(Constants.MODEL_CONNECTION);
        // notification = (NotificationModel) getIntent().getSerializableExtra("notificationModel");
        /*if (profile.status != null && profile.status.contains("inComming")) {
            resetConnectionState();
            linearLayout_incoming.setVisibility(View.VISIBLE);

            //   cancel.setVisibility(View.GONE);
        }*/

        if (null != connectionModel) {
            profile = connectionModel.getProfile();
        }
        setHeader();
        initView();
        if (profile.id != null) {
            getProfileData();
        }

        setFonts();

        button_connect.setOnClickListener(this);
        cancel.setOnClickListener(this);
        sentRequest.setOnClickListener(this);
        acceptRequest.setOnClickListener(this);
        dismiss.setOnClickListener(this);
        chat.setOnClickListener(this);
        inviteGroup.setOnClickListener(this);
        imageView_back.setOnClickListener(this);
        linearLayout_option.setOnClickListener(this);
    }

    private void initView() {
        aboutMe.setEnabled(false);

        textView_header = (TextView) findViewById(R.id.txt_header);
        textView_aboutMe = (TextView) findViewById(R.id.txt_about_me);
        textView_gender = (TextView) findViewById(R.id.txt_gender);
        textView_age = (TextView) findViewById(R.id.txt_date_of_birth);
        textView_location = (TextView) findViewById(R.id.txt_location);
        textView_interest = (TextView) findViewById(R.id.text_select);

        linearLayout_incoming = (LinearLayout) findViewById(R.id.people_profile_linearLayout_incoming);
        linearLayout_outgoing = (LinearLayout) findViewById(R.id.people_profile_linearLayout_outgoing);
        linearLayout_connected = (LinearLayout) findViewById(R.id.people_profile_linearLayout_connection);
        button_connect = (Button) findViewById(R.id.profile_connect_button);

        cancel = (Button) findViewById(R.id.profile_cancel_button);
        sentRequest = (Button) findViewById(R.id.profile_request_sent_button);
        acceptRequest = (Button) findViewById(R.id.profile_accept_request_button);
        dismiss = (Button) findViewById(R.id.profile_dismiss_button);
        chat = (Button) findViewById(R.id.profile_chat_button);
        inviteGroup = (Button) findViewById(R.id.profile_invite_group_button);

        imageView_back = (ImageView) findViewById(R.id.people_profile_imageView_back);

        linearLayout_option = (LinearLayout) findViewById(R.id.more_option_linearLayout);
    }

    private void setFonts() {
        textView_header.setTypeface(avenirNextRegular);
        textView_aboutMe.setTypeface(avenirNextRegular);
        textView_gender.setTypeface(avenirNextRegular);
        textView_age.setTypeface(avenirNextRegular);
        textView_location.setTypeface(avenirNextRegular);
        textView_interest.setTypeface(avenirNextRegular);

        button_connect.setTypeface(avenirNextRegular);
        cancel.setTypeface(avenirNextRegular);
        sentRequest.setTypeface(avenirNextRegular);
        acceptRequest.setTypeface(avenirNextRegular);
        dismiss.setTypeface(avenirNextRegular);
        chat.setTypeface(avenirNextRegular);
        inviteGroup.setTypeface(avenirNextRegular);
    }

    private void setHeader() {
        if (profile != null && profile.name != null) {
            header.setText(profile.name);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        addActions();
    }

    private void addActions() {
        addAction(QuickBloxServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION, new CreatePrivateChatSuccessAction());
        updateBroadcastActionList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    private void removeActions() {
        removeAction(QuickBloxServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION);
        updateBroadcastActionList();
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void getProfileData() {
        if (profile == null) {
            return;
        }
        showProgress();
        profileService.getProfile(profile.getServerId(), new AsyncResult<ProfileModel>() {
            @Override
            public void success(final ProfileModel profileModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        setProfileData(profileModel);
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

    private void displayImageByUrl(String publicUrl, ImageView imageView) {
        ImageLoader.getInstance().displayImage(publicUrl, imageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS, new ImageLoadingListener(imageView));
    }

    private void displayMaskedImageByUrl(String publicUrl, MaskedImageView maskedImageView) {
        ImageLoader.getInstance().displayImage(publicUrl, maskedImageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    private void displayImage(String publicUrl, ImageView imageView) {
        ImageLoader.getInstance().displayImage(publicUrl, imageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS_BLUR, new ImageLoadingListener(imageView));
    }

    private void setProfileData(ProfileModel model) {
        header.setText(model.name);
        profile = model;

        if (model.picUrl != null && !model.picUrl.isEmpty() && model.picUrl.contains("http")) {
            displayImageByUrl(model.picUrl, blur_image);
            displayMaskedImageByUrl(model.picUrl, masked_profileImage);
            imageURL = model.picUrl;
        } else {
            displayMaskedImageByUrl("", masked_profileImage);
            /*Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_icon);
            masked_profileImage.setImageBitmap(image);

            Bitmap image_to_blur = BitmapFactory.decodeResource(getResources(), R.drawable.default_blur_profile_icon);
            blur_image.setImageBitmap(image_to_blur);*/
        }

        aboutMe.setText(model.about);
        gender.setText(model.gender);
        age.setText(String.valueOf(model.age) + " years");
        location.setText(model.location.name);
        PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
        helper.savePreference(PreferenceHelper.USER_PROFILE_ID, model.getServerId());
        resetConnectionState();
        if (model.myConnectionStatus == null) {
            button_connect.setVisibility(View.VISIBLE);
        } else if (model.myConnectionStatus != null && model.myConnectionStatus.contains("outGoing")) {
            linearLayout_outgoing.setVisibility(View.VISIBLE);
        } else if (model.myConnectionStatus != null && model.myConnectionStatus.contains("active") && model.chat.getId() >= 0) {
            linearLayout_connected.setVisibility(View.VISIBLE);
        } else if (model.myConnectionStatus != null && model.myConnectionStatus.contains("inComming")) {
            linearLayout_incoming.setVisibility(View.VISIBLE);
        }

        initInterestAdapter(model);
        //   initTopicAdapter(model);
    }

    void resetConnectionState() {
        button_connect.setVisibility(View.GONE);
        linearLayout_incoming.setVisibility(View.GONE);
        linearLayout_outgoing.setVisibility(View.GONE);
        linearLayout_connected.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initInterestAdapter(ProfileModel profile) {
        interestsInput = new PageInput();
        interestsList = new ArrayList<>();
        interestAdapter = new InterestAdapter(interestsList, interestsList, null, null);
        initializeInterests(profile);
        recyclerViewInterest.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewInterest.setAdapter(interestAdapter);
    }

    /*private void initTopicAdapter(ProfileModel profile) {
        tagInput = new PageInput();
        tagList = new ArrayList<>();
        tagAdapter = new TagAdapter(tagList, tagList, null, null);
        initializeTopics(profile);
        recyclerViewTopics.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTopics.setAdapter(tagAdapter);
    }*/

    private void initializeInterests(ProfileModel profile) {

        interestsInput.pageNo = 1;
        if (scrollListenerInterest != null) {
            scrollListenerInterest.reset();
        }

        interestsList.clear();
        loadInterests(profile);
    }

    private void loadInterests(ProfileModel profile) {
        List<InterestModel> selectedInterests = profile.interests;
        for (InterestModel item : selectedInterests) {
            item.isSelected = true;
            interestsList.add(item);
        }
        interestAdapter.setModified();
    }

    private void acceptConnectionRequest(final ProfileModel profile) {
        ConnectionModel model = new ConnectionModel();
        model.setServerId(profile.getServerId());
        model.status = "active";
        showProgress();
        connectionService.acceptConnectionRequest(model, new AsyncResult<ConnectionModel>() {
            @Override
            public void success(ConnectionModel connectionModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetConnectionState();
                        linearLayout_connected.setVisibility(View.VISIBLE);
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

    /* private void initializeTopics(ProfileModel profile) {

         tagInput.pageNo = 1;
         if (scrollListenerTopic != null) {
             scrollListenerTopic.reset();
         }

         tagList.clear();
         loadTopics(profile);
     }

     private void loadTopics(ProfileModel profile) {

         List<TagModel> selectedTags = profile.tags;
         for (TagModel item : selectedTags) {
             item.isSelected = true;
             tagList.add(item);
         }

         tagAdapter.setModified();
     }
 */
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.profile_connect_button):
                linkWithProfile(profile);
                break;
            case (R.id.profile_cancel_button):
                cancelConnectionRequest(profile);
                break;
            case (R.id.profile_accept_request_button):
                acceptConnectionRequest(profile);
                break;
            case (R.id.profile_dismiss_button):
                cancelConnectionRequest(profile);
                break;
            case (R.id.profile_chat_button):
                if (profile != null) {
                    try {
                        startPrivateChatActivity(getPrivateChatDialog(connectionModel.profile.getChat().getId()
                                , AppSession.getSession().getUser().getQbId()));
                    }catch (NullPointerException ex){
                        startChat(connectionModel);
                        Log.e(TAG,ex.getMessage());
                        ex.printStackTrace();
                    }
                } else {

                }
                break;
            case (R.id.people_profile_imageView_back):
                finish();
                break;
            case (R.id.more_option_linearLayout):
                reportToAbuseProfile(profile);
                break;
            case (R.id.profile_invite_group_button):
                Intent intent=new Intent(DisplayProfile.this, InviteToGroupActivity.class);
                intent.putExtra("Profile",profile);
                startActivity(intent);
                break;

        }

    }

    public void reportToAbuseProfile(ProfileModel profile) {
        ArrayList<Integer> hideOptions = new ArrayList<>();
        hideOptions.add(DialogUtil.OPTION_DELETE);
        if(profile.myConnectionStatus !=null && profile.myConnectionStatus.contains("active"))
        {

        }else{
            hideOptions.add(DialogUtil.OPTION_UNFRIEND);
        }
        DialogUtil.showOptionsDialog(DisplayProfile.this, new AsyncResult<Integer>() {
            @Override
            public void success(Integer option) {
                if (option == DialogUtil.OPTION_REPORT_ABUSE) {
                    reportAbuseProfile();
                } else if (option == DialogUtil.OPTION_CANCEL) {

                } else if (option == DialogUtil.OPTION_UNFRIEND) {
                    unfriendProfile();
                } else {
                    return;
                }
            }

            @Override
            public void error(String error) {

            }
        }, hideOptions);
    }

    public void reportAbuseProfile() {
        ReportBugModel reportAbuse = new ReportBugModel();
        reportAbuse.setServerId(profile.getServerId());
        reportAbuse.setAbuseFor("profile");
        reportAbuse.setName(profile.getName());
        showProgress();
        _ReportBugService.reportBug(reportAbuse, asyncResult_reportAbuse);
    }

    AsyncResult<ReportBugModel> asyncResult_reportAbuse = new AsyncResult<ReportBugModel>() {
        @Override
        public void success(ReportBugModel reportBugModel) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                }
            });
        }

        @Override
        public void error(final String error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    hideProgress();
                }
            });
        }
    };

    private void cancelConnectionRequest(ProfileModel profile) {
        ConnectionModel model = new ConnectionModel();
        model.profile = profile;
        connectionService.cancelLinkConnection(model, new AsyncResult<ConnectionModel>() {
            @Override
            public void success(ConnectionModel connectionModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast("Request has been cancelled");
                        resetConnectionState();
                        button_connect.setVisibility(View.VISIBLE);
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

    private void linkWithProfile(ProfileModel profile) {
        ConnectionModel model = new ConnectionModel();
        model.profile = profile;
        connectionService.linkConnection(model, new AsyncResult<ConnectionModel>() {
            @Override
            public void success(ConnectionModel connectionModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetConnectionState();
                        linearLayout_outgoing.setVisibility(View.VISIBLE);
                        ToastUtils.longToast("Connect Successfully");
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

    public class ImageLoadingListener extends SimpleImageLoadingListener {

        private String imageUrl;
        private ImageView imageView;

        public ImageLoadingListener(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            super.onLoadingStarted(imageUri, view);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            imageUrl = null;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, final Bitmap loadedBitmap) {
            initMaskedImageView(loadedBitmap);
            this.imageUrl = imageUri;
        }

        private void initMaskedImageView(Bitmap loadedBitmap) {
            Bitmap blurred_image = makeImageBlur(loadedBitmap);
            imageView.setImageBitmap(blurred_image);

        }

        public Bitmap makeImageBlur(Bitmap bitmapImage) {
            Bitmap outputBitmap = null;
            try {
                if (null == bitmapImage)
                    return null;

                outputBitmap = Bitmap.createBitmap(bitmapImage);
                final RenderScript renderScript = RenderScript.create(getApplicationContext());
                Allocation tmpIn = Allocation.createFromBitmap(renderScript, bitmapImage);
                Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);
                //Intrinsic Gausian blur filter
                ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
                theIntrinsic.setRadius(BLUR_RADIUS);
                theIntrinsic.setInput(tmpIn);
                theIntrinsic.forEach(tmpOut);
                tmpOut.copyTo(outputBitmap);
                renderScript.destroy();
            } catch (Exception e) {
                Log.d("Exception ", "method exception " + e.getMessage());
            }
            return outputBitmap;
        }
    }

    @OnClick(R.id.image_view_profile)
    public void previewImage() {
        setupImageFragment();
    }

    private void setupImageFragment() {
        // Transition for fragment1
        Slide slideTransition;
        PreviewImageFragment previewImageFragment = PreviewImageFragment.newInstance(imageURL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            slideTransition = new Slide(Gravity.LEFT);
            slideTransition.setDuration(1000);

            // Create fragment and define some of it transitions
            previewImageFragment.setReenterTransition(slideTransition);
            previewImageFragment.setExitTransition(slideTransition);
            previewImageFragment.setSharedElementEnterTransition(new ChangeBounds());
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(previewImageFragment, PREVIEW_IMAGE_FRAGMENT);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_icon_layout, menu);
        MenuItem addUser = menu.findItem(R.id.edit_marker);
        return super.onCreateOptionsMenu(menu);
    }


    private void startChat(ConnectionModel connectionModel) {
        opponentUser = new ChatUserModel();
        opponentUser.setUserId(connectionModel.profile.chat.id);


        Log.d(TAG,String.format("StartChat: OpponentUserId - %d",connectionModel.profile.getChat().id));

        showProgress();
        QBCreatePrivateChatCommand.start(this, opponentUser);

        /*DialogUserModel dialogOccupant = dataManager.getDialogUsersRepository().get(new PageQuery().add("userId", opponentUser.getUserId()));
      if (dialogOccupant != null && dialogOccupant.getDialog() != null) {
            opponentUser = dataManager.getChatUserRepository().get(new PageQuery().add("userId", opponentUser.getUserId()));
            PrivateDialogActivity.start(this, opponentUser, dialogOccupant.getDialog());
            boolean fromChat = getIntent().getBooleanExtra("fromChat", false);
            if (fromChat)
                finish();
        } else {
            showProgress();
            QBCreatePrivateChatCommand.start(this, opponentUser);
        }*/
    }

    protected void startPrivateChat(QBChatDialog qbDialog) {
        opponentUser = dataManager.getChatUserRepository().get(new PageQuery().add("userId", opponentUser.getUserId()));
        PrivateDialogActivity.start(this, opponentUser, ChatUtils.createLocalDialog(qbDialog));
        boolean fromChat = getIntent().getBooleanExtra("fromChat", false);
        if (fromChat)
            finish();
    }

    private class CreatePrivateChatSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) throws Exception {
            hideProgress();
            QBChatDialog qbDialog = (QBChatDialog) bundle.getSerializable(QuickBloxServiceConsts.EXTRA_DIALOG);
            startPrivateChat(qbDialog);
        }
    }

    public void unfriendProfile() {
        showProgress();
        connectionService.deLinkConnection(connectionModel, new AsyncNotify() {
            @Override
            public void success() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetConnectionState();
                        button_connect.setVisibility(View.VISIBLE);
                        hideProgress();
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                        hideProgress();
                    }
                });
            }
        });
    }

    DialogModel getPrivateChatDialog( int occupantId1, int occupantId2) throws NullPointerException{

        PageQuery query = new PageQuery();
        query.add(Constants.QUERY_KEY_OCCUPANT_1_ID,occupantId1);
        query.add(Constants.QUERY_KEY_OCCUPANT_2_ID,occupantId2);

        PageInput input = new PageInput(query);


        List<DialogUserModel> dialogUserModels = dataManager.getDialogUsersRepository().page(input).items;

        for (DialogUserModel dialogUserModel:dialogUserModels) {
            DialogModel dialogModel = dataManager.getDialogRepository().get(new PageQuery(Constants.QUERY_KEY_DIALOG_ID,dialogUserModel.getDialogId()));
            if(dialogModel.getType() == DialogModel.Type.PRIVATE){
                return dialogModel;
            }
        }


        throw new NullPointerException("Didn't find any dialog");
    }

    private void startPrivateChatActivity(DialogModel dialog) {
        UserModel chatUser = AppSession.getSession().getUser();
        PageInput input = new PageInput();
        input.query.add("dialogId", dialog.getServerId());
        List<DialogUserModel> occupantsList = dataManager.getDialogUsersRepository().page(input).items;

        ChatUserModel opponent = ChatUtils.getOpponentFromPrivateDialog(ChatUserUtils.createLocalUser(ChatUserUtils.createQBUser(chatUser)), occupantsList);
        if (!TextUtils.isEmpty(dialog.getServerId())) {
            PrivateDialogActivity.start(this, opponent, dialog);
        }
    }
}
