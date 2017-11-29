package com.trutek.looped.ui.communityDashboard.myCommunities.display;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommentModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.InviteModel;
import com.trutek.looped.data.contracts.models.MemberModel;
import com.trutek.looped.data.contracts.models.NestedRecyclerViewModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.ReportBugModel;
import com.trutek.looped.data.contracts.services.IActivityService;
import com.trutek.looped.data.contracts.services.ICommentService;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.data.contracts.services.IReportBugService;
import com.trutek.looped.data.impl.entities.Community;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.activity.create.CreateActivity;
import com.trutek.looped.ui.activity.display.DisplayActivity;
import com.trutek.looped.ui.activity.display.discussion.DiscussionActivity;
import com.trutek.looped.ui.activity.display.discussion.DiscussionAdapter;
import com.trutek.looped.ui.authenticate.SignUpActivity;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.edit.EditCommunity;
import com.trutek.looped.ui.customcontrol.CustomEditText;
import com.trutek.looped.ui.profile.display.DisplayProfile;
import com.trutek.looped.utils.DialogUtil;
import com.trutek.looped.utils.KeyboardUtils;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.image.ImageLoaderUtils;
import com.trutek.looped.utils.listeners.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class DisplayCommunity extends BaseAppCompatActivity implements View.OnClickListener {

    public static final int EDIT_CODE = 1;
    public static final int CREATE_ACTIVITY = 2;
    public static final int SELECT_CONNECTION_ACTIVITY = 3;
    public static final int REQUEST_COMMUNITY_DISCUSSION = 4;
    public static final int ADD_MEMBERS_TO_COMMUNITY = 5;
    public static final int PAST_ACTIVITY = 6;


    @Inject
    ICommunityService communityService;
    @Inject
    IActivityService activityService;

    @Inject
    IProfileService _ProfileService;
    @Inject
    ICommentService commentService;

    @Inject
    IReportBugService _ReportBugService;
    @BindView(R.id.txt_join)
    TextView txt_join;
    MaskedImageView communityImage;
    Button button_join;


    TextView textView_communityName, textView_communityDesc,textView_defaultUpcomingEvents;
    RecyclerView recyclerViewMembers;
    RecyclerView recyclerViewActivities, recyclerView_comments;
    ImageView imageView_back, imv_description;
    Button button_reply, create_event,past_events;
    CustomEditText edit_txt_description;
    TextView textView_description_text, textView_description_title, textView_member_text, textView_upcomingEvent_text, textView_discussion_text;
    TextView textView_member_all, textView_discussion_all, textView_discussion_default;
    ImageView imageView_edit_community;
    ProgressBar progressBar_comments;
    LinearLayout linearLayout_reportAbuse;

    private CommunityModel community;
    private ArrayList<MemberModel> members;
    private ArrayList<ActivityModel> activities;
    ProfileModel profileModel;

    private MembersAdapter membersAdapter;
    private ActivityAdapter activityAdapter;


    private ArrayList<CommentModel> comments;
    private ArrayList<ArrayList<CommentModel>> subComments;
    private DiscussionAdapter discussionAdapter;
    private ActivityModel activity;

    RelativeLayout relativeLayout_discussion;

    boolean isReply = false;
    int replyToComment = -1, isPublicCommunites;

    public class OpenForm {
        public static final int DISCOVER_COMMUNITY = 0;
        public static final int PROFILE_CREATE = 1;
        public static final int HOME_FRAGMENT = 2;
        public static final int CREATED_COMM_FRAG = 3;
        public static final int JOINED_COMM_FRAG = 4;
        public static final int JOINED__FRAG = 5;
    }

    private int OPEN_FORM,OPEN_FORM_COMMUNITY;

    @Override
    protected int getContentResId() {
        return R.layout.activity_display_community;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();

        initFields();
        setFonts();
        // button_reply.setOnClickListener(this);
        isPublicCommunites = getIntent().getIntExtra("OPEN_FROM", 1);
        getCommunityData();
        getActivities();
        imageView_back.setOnClickListener(this);
        textView_member_all.setOnClickListener(this);
        linearLayout_reportAbuse.setOnClickListener(this);
        recyclerViewMembers.setOnClickListener(this);
        textView_discussion_all.setOnClickListener(this);
        imageView_edit_community.setOnClickListener(this);
        create_event.setOnClickListener(this);
        past_events.setOnClickListener(this);
        linearLayout_reportAbuse.setOnClickListener(this);
        edit_txt_description.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (isPublicCommunites == 0) {
                    alertMessages();
                } else {
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        if (!isReply) {
                            createComment();
                        } else {
                            replyComment(edit_txt_description.getText().toString());
                        }

                        KeyboardUtils.hideKeyboard(edit_txt_description);
                        edit_txt_description.setHint(getString(R.string.comment_text_hint));
                        edit_txt_description.setText("");
                        isReply = false;
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void initViews() {

        textView_communityName = (TextView) findViewById(R.id.display_community_textView_communityName);
        textView_communityDesc = (TextView) findViewById(R.id.display_community_textView_communityDesc);
        textView_description_text = (TextView) findViewById(R.id.display_community_editText_description);
        textView_member_text = (TextView) findViewById(R.id.display_community_textView_member_title);
        textView_member_all = (TextView) findViewById(R.id.display_community_textView_show_member);
        textView_discussion_text = (TextView) findViewById(R.id.discussion_activity_textView_discussion_title);
        textView_discussion_all = (TextView) findViewById(R.id.discussion_activity_textView_show_discussion);
        textView_upcomingEvent_text = (TextView) findViewById(R.id.display_community_textView_upcomingEvent_title);
        textView_discussion_default = (TextView) findViewById(R.id.content_discussion_textView_default);
        textView_description_title = (TextView) findViewById(R.id.display_community_textView_description_title);
        textView_defaultUpcomingEvents = (TextView) findViewById(R.id.default_text_upcoming_events);
        imageView_edit_community = (ImageView) findViewById(R.id.display_community_image_edit);
        edit_txt_description = (CustomEditText) findViewById(R.id.edit_text_discussion_activity);

        recyclerViewMembers = (RecyclerView) findViewById(R.id.recycler_view_community_member);
        recyclerViewActivities = (RecyclerView) findViewById(R.id.recycler_view_community_activities);
        recyclerView_comments = (RecyclerView) findViewById(R.id.recycler_discussion_activity);

        button_join = (Button) findViewById(R.id.display_community_button_join);
        button_reply = (Button) findViewById(R.id.content_description_button_reply);
        create_event = (Button) findViewById(R.id.button_create_new_event);
        past_events = (Button) findViewById(R.id.button_past_event);


        imageView_back = (ImageView) findViewById(R.id.display_community_imageView_back);
        imv_description = (ImageView) findViewById(R.id.image_edit_discussion_activity);

        communityImage = (MaskedImageView) findViewById(R.id.image_view_community);

        progressBar_comments = (ProgressBar) findViewById(R.id.content_discussion_progressBar);
        linearLayout_reportAbuse = (LinearLayout) findViewById(R.id.more_option_linearLayout);

        relativeLayout_discussion = (RelativeLayout) findViewById(R.id.display_community_relativeLayout_discussion);

    }

    void replyComment(String text) {
        CommentModel commentModel = new CommentModel();
        commentModel.text = text;
        commentService.createSubComment(commentModel, comments.get(replyToComment).getServerId(), new AsyncResult<CommentModel>() {
            @Override
            public void success(CommentModel commentModel) {
                subComments.get(replyToComment).add(commentModel);
                comments.get(replyToComment).setThreadCount(comments.get(replyToComment).getThreadCount() + 1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        discussionAdapter.notifyItemChanged(replyToComment);
                    }
                });
            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    void setFonts() {
        textView_communityName.setTypeface(avenirNextRegular);
        textView_communityDesc.setTypeface(avenirNextRegular);
        textView_description_text.setTypeface(avenirNextRegular);
        textView_member_text.setTypeface(avenirNextRegular);
        textView_member_all.setTypeface(avenirNextRegular);
        textView_discussion_text.setTypeface(avenirNextRegular);
        textView_discussion_all.setTypeface(avenirNextRegular);
        textView_upcomingEvent_text.setTypeface(avenirNextRegular);
        textView_discussion_default.setTypeface(avenirNextRegular);
        textView_description_title.setTypeface(avenirNextRegular);
        edit_txt_description.setTypeface(avenirNextRegular);
        textView_defaultUpcomingEvents.setTypeface(avenirNextRegular);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.display_community_imageView_back):
                if(OPEN_FORM_COMMUNITY>0)
                {
                    community.isSelected=false;
                    Intent intent_back =new Intent();
                    intent_back.putExtra("communityModel", community);
                    setResult(RESULT_OK,intent_back);
                    finish();
                }else {
                    Intent intent_back = new Intent();
                    setResult(RESULT_OK, intent_back);
                    finish();
                }
                break;
            case (R.id.display_community_textView_show_member):
                if (isPublicCommunites == 0) {
                    alertMessages();

                } else if (community.isSelected) {
                    Intent intent = new Intent(DisplayCommunity.this, DisplayMembersActivity.class);
                    intent.putExtra("memberModel", members);
                    intent.putExtra("CommunityID", community.getServerId());
                    startActivityForResult(intent, ADD_MEMBERS_TO_COMMUNITY);
                } else {
                    return;
                }
                break;
            case (R.id.discussion_activity_textView_show_discussion):
                if (isPublicCommunites == 0) {
                    alertMessages();
                } else {
                    Intent intentCommunity = new Intent(DisplayCommunity.this, DiscussionActivity.class);
                    intentCommunity.putExtra(Constants.MODEL_COMMUNITY, community);
                    startActivity(intentCommunity);
                }
                break;
            case (R.id.more_option_linearLayout):
                if (isPublicCommunites == 0) {
                    alertMessages();
                } else {
                    reportToAbuseCommunity();
                }
                break;
            case (R.id.edit_text_discussion_activity):
                if (isPublicCommunites == 0) {
                    alertMessages();
                } else {
                    return;
                }
                break;
            case (R.id.display_community_image_edit):
                Intent intent = new Intent(DisplayCommunity.this, EditCommunity.class);
                intent.putExtra("CommunityModel", community);
                startActivityForResult(intent, CREATE_ACTIVITY);
                break;
            case (R.id.button_create_new_event):
                if (isPublicCommunites == 0) {
                    alertMessages();
                } else {
                    Intent i = new Intent(DisplayCommunity.this, CreateActivity.class);
                    i.putExtra("communityModel", community);
                    startActivityForResult(i, CREATE_ACTIVITY);
                }
                break;
            case (R.id.button_past_event):
                {
                    Intent i = new Intent(DisplayCommunity.this, PastEventsActivity.class);
                    i.putExtra("communityId", community.getServerId());
                    startActivityForResult(i, PAST_ACTIVITY);
                }
                break;
        }
    }

    public void reportToAbuseCommunity() {
        ArrayList<Integer> hideOptions = new ArrayList<>();
        hideOptions.add(DialogUtil.OPTION_UNFRIEND);
        hideOptions.add(DialogUtil.OPTION_DELETE);

        DialogUtil.showOptionsDialog(DisplayCommunity.this, new AsyncResult<Integer>() {
            @Override
            public void success(Integer option) {
                if (option == DialogUtil.OPTION_REPORT_ABUSE) {
                    reportAbuseCommunity();
                } else if (option == DialogUtil.OPTION_CANCEL) {

                } else {
                    return;
                }
            }

            @Override
            public void error(String error) {

            }
        }, hideOptions);
    }


    public void alertMessages() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DisplayCommunity.this);
        alertDialogBuilder.setTitle("Wants to learn more?");
        alertDialogBuilder.setMessage("Create an account first");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent(DisplayCommunity.this, SignUpActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("Not Yet",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void reportAbuseCommunity() {
        ReportBugModel reportAbuse = new ReportBugModel();
        reportAbuse.setServerId(community.getServerId());
        reportAbuse.setAbuseFor("community");
        reportAbuse.setName(community.getSubject());
        showProgress();
        _ReportBugService.reportBug(reportAbuse, asyncResult_reportAbuse);
    }

    private void initAdapters() {
        members = new ArrayList<>();
        activities = new ArrayList<>();

        membersAdapter = new MembersAdapter(this, members, asyncResult_profile);
        activityAdapter = new ActivityAdapter(activities);

        final LinearLayoutManager layoutManagerMembers = new LinearLayoutManager(this);

        layoutManagerMembers.setOrientation(LinearLayoutManager.HORIZONTAL);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerViewMembers.setLayoutManager(layoutManagerMembers);
        recyclerViewMembers.setAdapter(membersAdapter);

        recyclerViewActivities.setLayoutManager(layoutManager);
        recyclerViewActivities.setAdapter(activityAdapter);
    }

    private void initFields() {
        community = (CommunityModel) getIntent().getSerializableExtra("communityModel");
        comments = new ArrayList<>();
        subComments = new ArrayList<>();

        if (getIntent().getIntExtra("OPEN_FROM", 10) == OpenForm.DISCOVER_COMMUNITY) {
            OPEN_FORM = OpenForm.DISCOVER_COMMUNITY;
        } else if (getIntent().getIntExtra("OPEN_FROM", 10) == OpenForm.PROFILE_CREATE) {
            OPEN_FORM = OpenForm.PROFILE_CREATE;
        } else if (getIntent().getIntExtra("OPEN_FROM", 10) == OpenForm.HOME_FRAGMENT) {
            OPEN_FORM = OpenForm.HOME_FRAGMENT;
        } else if (getIntent().getIntExtra("OPEN_FROM", 10) == OpenForm.CREATED_COMM_FRAG) {
            OPEN_FORM = OpenForm.CREATED_COMM_FRAG;
        } else if (getIntent().getIntExtra("OPEN_FROM", 10) == OpenForm.JOINED_COMM_FRAG) {
            OPEN_FORM = OpenForm.JOINED_COMM_FRAG;
        } else if (getIntent().getIntExtra("OPEN_FROM", 10) == OpenForm.JOINED__FRAG) {
            OPEN_FORM_COMMUNITY = OpenForm.JOINED__FRAG;
        }
        setHeader();
        initListeners();
        initAdapters();
        initCommentAdapter();
//        allComments();
    }

    /* public void initCommentAdapter() {
         discussionAdapter = new DiscussionAdapter(comments,
                 subComments,
                 interestSelectedActionListeners,
                 replyListener,subCommentListener,
                 commentOptionListener, subCommentOptionListener);
         LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
         recyclerView_comments.setLayoutManager(linearLayoutManager);
         recyclerView_comments.setAdapter(discussionAdapter);
     }*/

    private void createComment() {
        final CommentModel comment = new CommentModel();
        comment.text = edit_txt_description.getText().toString();
        comment.community = community;
        comment.date = new Date();

        commentService.createComment(comment, new AsyncResult<CommentModel>() {
            @Override
            public void success(final CommentModel comment) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        comments.add(0, comment);
                        discussionAdapter.notifyDataSetChanged();
                        edit_txt_description.setText("");
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
   /* @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.button_reply_discussion_activity):
                Toast.makeText(this,"Testing",Toast.LENGTH_SHORT).show();

                break;
        }
    }*/


    private void allComments() {
        showCommentProgress();
        commentService.allCommunityComments(community.getServerId(), new AsyncResult<Page<CommentModel>>() {
            @Override
            public void success(final Page<CommentModel> commentPage) {


                comments.addAll(commentPage.items);

                Collections.sort(comments, new Comparator<CommentModel>() {
                    @Override
                    public int compare(CommentModel o1, CommentModel o2) {
                        return o2.date.compareTo(o1.date);
                    }
                });

                for (int i = 0; i < comments.size(); i++) {
                    subComments.add(new ArrayList<CommentModel>());

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        discussionAdapter.notifyDataSetChanged();
                        hideProgress();
                        hideCommentProgress();
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
                        hideCommentProgress();
                    }
                });
            }
        });
    }


    void showCommentProgress() {
        progressBar_comments.setVisibility(View.VISIBLE);
        recyclerView_comments.setVisibility(View.GONE);
        textView_discussion_default.setVisibility(View.GONE);
    }

    void hideCommentProgress() {
        progressBar_comments.setVisibility(View.GONE);
        if (discussionAdapter.getItemCount() == 0) {
            recyclerView_comments.setVisibility(View.GONE);
            textView_discussion_default.setVisibility(View.VISIBLE);
        } else {
            recyclerView_comments.setVisibility(View.VISIBLE);
            textView_discussion_default.setVisibility(View.GONE);
        }

    }

    private void initListeners() {
        recyclerViewActivities.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isPublicCommunites == 0) {
                    alertMessages();
                } else {
                    ActivityModel activity = activities.get(position);
                    DisplayActivity.start(DisplayCommunity.this, activity);
                }
            }
        }));
    }

    private void setHeader() {
//        header.setText(community.subject);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (OPEN_FORM == OpenForm.DISCOVER_COMMUNITY || OPEN_FORM == OpenForm.PROFILE_CREATE) {
            return super.onCreateOptionsMenu(menu);
        }
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_icon_layout, menu);
        MenuItem addUser = menu.findItem(R.id.edit_marker);
        if (getIntent().getIntExtra("OPEN_FROM", 10) == OpenForm.JOINED_COMM_FRAG) {
            addUser.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public void onBackPressed() {
        setResult();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.edit_marker:
                Intent intent = new Intent(getApplicationContext(), EditCommunity.class);
                intent.putExtra("CommunityModel", community);
                startActivityForResult(intent, EDIT_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCommunityData() {
        if (community == null) {
            return;
        }
        showProgress();
        communityService.getCommunity(community.getServerId(), new AsyncResult<CommunityModel>() {
            @Override
            public void success(final CommunityModel communityModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        communityModel.isSelected = community.isSelected;
                        communityModel.isMine = community.isMine;
                        community = communityModel;
                        setCommunityData();
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

    private void setCommunityData() {
       /* community.tags = new ArrayList<>();
        community.interests = new ArrayList<>();
        community.tags.addAll(communityModel.getTags());
        community.interests.addAll(communityModel.getInterests());
        community.setAdmin(communityModel.getAdmin());*/

        if (community.picUrl != null && !community.picUrl.isEmpty() && community.picUrl.contains("http")) {
            displayImageByUrl(community.picUrl, communityImage);
        } else {
            communityImage.setImageDrawable(getResources().getDrawable(R.drawable.background_round_color_third));
        }

        if (community.isSelected) {
            button_join.setText(getString(R.string.ipc_text_leave));
        } else {
            button_join.setText(getString(R.string.text_join));
        }
        if (community.subject != null) {
            textView_communityName.setText(community.subject);
        }
        if (community.location != null) {
            textView_communityDesc.setText(community.location.name);
        }
        profileModel = _ProfileService.getMyProfile(null);
        if (community.getAdmin().getServerId().equals(profileModel.getServerId()) && (profileModel.getServerId() != null)) {
            button_join.setVisibility(View.GONE);
            linearLayout_reportAbuse.setVisibility(View.GONE);
            imageView_edit_community.setVisibility(View.VISIBLE);
        } else {
            button_join.setVisibility(View.VISIBLE);
            imageView_edit_community.setVisibility(View.GONE);
            linearLayout_reportAbuse.setVisibility(View.VISIBLE);
        }
        if (isPublicCommunites != 0 && community.isSelected) {
            create_event.setVisibility(View.VISIBLE);
        } else {
            create_event.setVisibility(View.GONE);
        }

        textView_description_text.setText(community.body);
        textView_member_text.setText(String.valueOf(community.getMembers().size()) + " " + getString(R.string.text_members));
        members.clear();
        members.addAll(community.members);
        membersAdapter.notifyDataSetChanged();
        showDiscussionLayout(community.isSelected);
    }


    @OnClick(R.id.fab)
    public void fabClick() {
        Intent intent = new Intent(this, CreateActivity.class);
        intent.putExtra("communityModel", community);
        startActivityForResult(intent, CREATE_ACTIVITY);
    }

    /*@OnClick(R.id.tv_member_plus_icon)
    public void addMember(){
        Intent intent = new Intent(this, SelectConnectionActivity.class);
        intent.putExtra("OPEN_FROM", 1);
        startActivityForResult(intent, SELECT_CONNECTION_ACTIVITY);
    }*/

    @OnClick(R.id.display_community_button_join)
    public void join() {
        showProgress();
        String joinButtonText = button_join.getText().toString();
        if (!community.isSelected) {
            joinnedCommunity();
        } else {
            leaveCommunity();
        }
    }

    public void joinnedCommunity() {
        community.profileIds.add(profileModel.getServerId());
        communityService.joinCommunity(community, new AsyncResult<CommunityModel>() {
            @Override
            public void success(CommunityModel communityModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button_join.setText(getString(R.string.ipc_text_leave));
                        community.isSelected = true;
                        showDiscussionLayout(community.isSelected);
                        hideProgress();
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

    public void leaveCommunity() {
        CommunityModel communityModel = new CommunityModel();
        communityModel.setServerId(community.getServerId());
        String profileId = profileModel.getServerId();

        communityService.leaveCommunity(profileId, communityModel, new AsyncNotify() {
            @Override
            public void success() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button_join.setText(getString(R.string.ipc_text_join));
                        community.isSelected = false;
                        showDiscussionLayout(community.isSelected);
                        hideProgress();
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

    public void getActivities() {
        activityService.activitiesByCommunity(community, new AsyncResult<Page<ActivityModel>>() {
            @Override
            public void success(final Page<ActivityModel> activityModelPage) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setActivitiesData(activityModelPage.items);


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case EDIT_CODE:
                    community = (CommunityModel) data.getSerializableExtra("communityModel");
                    setCommunityData();
                    break;

                case CREATE_ACTIVITY:
                    getActivities();
                    break;
                case SELECT_CONNECTION_ACTIVITY:
                    InviteModel invite = (InviteModel) data.getSerializableExtra("inviteModel");

                    break;
                case ADD_MEMBERS_TO_COMMUNITY:
                    community.members = (ArrayList<MemberModel>) data.getSerializableExtra("memberList");
                    members.addAll(community.members);
                    membersAdapter.notifyDataSetChanged();
                    break;
                case REQUEST_COMMUNITY_DISCUSSION:
                    if (null != data) {
                        comments.clear();
                        subComments.clear();
                        ArrayList<CommentModel> models = (ArrayList<CommentModel>) data.getSerializableExtra(Constants.INTENT_KEY_COMMENTS);
                        ArrayList<ArrayList<CommentModel>> subModels = (ArrayList<ArrayList<CommentModel>>) data.getSerializableExtra(Constants.INTENT_KEY_SUB_COMMENTS);
                        comments.addAll(models);
                        subComments.addAll(subModels);
                        discussionAdapter.notifyDataSetChanged();
                    }
                    break;

            }
        }
    }

    private void InviteMembers(InviteModel model) {
       /* communityService.inviteMembersIntoCommunity(null,null);
        ToastUtils.longToast("successfully invited");*/
    }


    public void initCommentAdapter() {
        discussionAdapter = new DiscussionAdapter(comments,
                subComments,
                interestSelectedActionListeners,
                replyListener, subCommentListener,
                commentOptionListener, subCommentOptionListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView_comments.setLayoutManager(linearLayoutManager);
        recyclerView_comments.setAdapter(discussionAdapter);
    }

    OnActionListener<NestedRecyclerViewModel> subCommentOptionListener = new OnActionListener<NestedRecyclerViewModel>() {
        @Override
        public void notify(final NestedRecyclerViewModel nestedRecyclerViewModel) {
            ArrayList<Integer> hideOptions = new ArrayList<>();
            hideOptions.add(DialogUtil.OPTION_UNFRIEND);

            if (subComments.get(nestedRecyclerViewModel.getParentPosition()).get(nestedRecyclerViewModel.getChildPosition()).getProfile().getServerId().equals(profileModel.getServerId())) {
                hideOptions.add(DialogUtil.OPTION_REPORT_ABUSE);
            } else {
                hideOptions.add(DialogUtil.OPTION_DELETE);
            }


            DialogUtil.showOptionsDialog(DisplayCommunity.this, new AsyncResult<Integer>() {
                @Override
                public void success(Integer option) {
                    if (option == DialogUtil.OPTION_DELETE) {
                        deleteSubComment(nestedRecyclerViewModel);
                    } else if (option == DialogUtil.OPTION_REPORT_ABUSE) {
                        reportAbuse(nestedRecyclerViewModel);
                    } else {
                        return;
                    }
                }

                @Override
                public void error(String error) {

                }
            }, hideOptions);
        }
    };

    void deleteSubComment(final NestedRecyclerViewModel nestedRecyclerViewModel) {
        showProgress();
        commentService.deleteSubComment(comments.get(nestedRecyclerViewModel.getParentPosition()), subComments.get(nestedRecyclerViewModel.getParentPosition()).get(nestedRecyclerViewModel.getChildPosition())
                , new AsyncNotify() {
                    @Override
                    public void success() {
                        subComments.get(nestedRecyclerViewModel.getParentPosition()).remove(nestedRecyclerViewModel.getChildPosition());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                discussionAdapter.notifyItemChanged(nestedRecyclerViewModel.getParentPosition());
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

    void reportAbuse(NestedRecyclerViewModel nestedRecyclerViewModel) {
        ReportBugModel reportAbuse = new ReportBugModel();
        reportAbuse.setServerId(subComments.get(nestedRecyclerViewModel.getParentPosition()).get(nestedRecyclerViewModel.getChildPosition()).getServerId());
        reportAbuse.setAbuseFor("Comment");
        reportAbuse.setName(community.getSubject());
        showProgress();
        _ReportBugService.reportBug(reportAbuse, asyncResult_reportAbuse);
    }

    OnActionListener<Integer> replyListener = new OnActionListener<Integer>() {
        @Override
        public void notify(Integer position) {
            edit_txt_description.setHint("Reply to " + comments.get(position).getProfile().getName());
            edit_txt_description.requestFocus();
            KeyboardUtils.showKeyboard(edit_txt_description);
            isReply = true;
            replyToComment = position;
        }
    };

    OnActionListener<Integer> subCommentListener = new OnActionListener<Integer>() {
        @Override
        public void notify(final Integer position) {
            commentService.getSubComments(comments.get(position), new AsyncResult<Page<CommentModel>>() {
                @Override
                public void success(Page<CommentModel> commentModelPage) {
                    subComments.get(position).clear();

                    subComments.get(position).addAll(commentModelPage.items);

                    Collections.sort(subComments.get(position), new Comparator<CommentModel>() {
                        @Override
                        public int compare(CommentModel o1, CommentModel o2) {
                            return o2.date.compareTo(o1.date);
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            discussionAdapter.notifyItemChanged(position);
                        }
                    });
                }

                @Override
                public void error(String error) {

                }
            });
        }
    };

    void setResult() {
        if (community.isSelected && OPEN_FORM == OpenForm.HOME_FRAGMENT) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
    }

    OnActionListener<Integer> commentOptionListener = new OnActionListener<Integer>() {
        @Override
        public void notify(final Integer position) {
            ArrayList<Integer> hideOptions = new ArrayList<>();
            hideOptions.add(DialogUtil.OPTION_UNFRIEND);
            if (comments.get(position).getProfile().getServerId().equals(profileModel.getServerId())) {
                hideOptions.add(DialogUtil.OPTION_REPORT_ABUSE);
            } else {
                hideOptions.add(DialogUtil.OPTION_DELETE);
            }
            DialogUtil.showOptionsDialog(DisplayCommunity.this, new AsyncResult<Integer>() {
                @Override
                public void success(Integer option) {
                    if (option == DialogUtil.OPTION_DELETE) {
                        deleteComment(position);
                    } else if (option == DialogUtil.OPTION_REPORT_ABUSE) {
                        reportAbuse(position);
                    } else {
                        return;
                    }
                }

                @Override
                public void error(String error) {

                }
            }, hideOptions);
        }
    };

    void deleteComment(final int position) {
        showProgress();
        commentService.deleteComment(comments.get(position), new AsyncNotify() {
            @Override
            public void success() {
                comments.remove(position);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        discussionAdapter.notifyDataSetChanged();
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

    void reportAbuse(int position) {
        ReportBugModel reportAbuse = new ReportBugModel();
        reportAbuse.setServerId(comments.get(position).getServerId());
        reportAbuse.setAbuseFor("Comment");
        reportAbuse.setName(community.getSubject());
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
    AsyncResult<MemberModel> asyncResult_profile = new AsyncResult<MemberModel>() {
        @Override
        public void success(MemberModel memberModelModel) {
            if (isPublicCommunites == 0) {
                alertMessages();
            } else {
                Intent intent = new Intent(DisplayCommunity.this, DisplayProfile.class);
                intent.putExtra("profileModel", memberModelModel.profile);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        @Override
        public void error(String error) {

        }
    };
    OnActionListener<CommentModel> interestSelectedActionListeners = new OnActionListener<CommentModel>() {
        @Override
        public void notify(final CommentModel commentModel) {
            commentModel.profileId = commentModel.profile.getServerId();
            commentModel.communityId = commentModel.community.getServerId();
            _ProfileService.getProfile(commentModel.profile.getServerId(), new AsyncResult<ProfileModel>() {

                @Override
                public void success(ProfileModel profileModel) {
                    commentModel.picUrl = profileModel.getPicUrl();
                    commentModel.name = profileModel.getName();
                }

                @Override
                public void error(String error) {

                }
            });
            // commentModel.activityId=commentModel.activity.id;
            commentService.saveCommentLocal(commentModel, new AsyncResult<CommentModel>() {
                @Override
                public void success(CommentModel commentModel) {

                }

                @Override
                public void error(String error) {

                }
            });
        }
    };

    private void displayImageByUrl(String publicUrl, MaskedImageView imageView) {
        ImageLoader.getInstance().displayImage(publicUrl, imageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    private void setActivitiesData(List<ActivityModel> activitiesList) {
        if(activitiesList.size()>0) {
            recyclerViewActivities.setVisibility(View.VISIBLE);
            textView_defaultUpcomingEvents.setVisibility(View.GONE);
        }else{
            recyclerViewActivities.setVisibility(View.GONE);
            textView_defaultUpcomingEvents.setVisibility(View.VISIBLE);
        }
        activities.clear();
        activities.addAll(activitiesList);
        activityAdapter.notifyDataSetChanged();
    }

    void showDiscussionLayout(boolean showDiscussion) {
        if (showDiscussion) {
            relativeLayout_discussion.setVisibility(View.VISIBLE);
            allComments();
        } else {
            relativeLayout_discussion.setVisibility(View.GONE);
        }
    }
}





















