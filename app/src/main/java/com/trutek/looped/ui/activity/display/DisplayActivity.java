package com.trutek.looped.ui.activity.display;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.apis.ICommentApi;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommentModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.MemberModel;
import com.trutek.looped.data.contracts.models.NestedRecyclerViewModel;
import com.trutek.looped.data.contracts.models.ReportBugModel;
import com.trutek.looped.data.contracts.services.IActivityService;
import com.trutek.looped.data.contracts.services.ICommentService;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.data.contracts.services.IReportBugService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.activity.display.discussion.DiscussionActivity;
import com.trutek.looped.ui.activity.display.discussion.DiscussionAdapter;
import com.trutek.looped.ui.activity.edit.EditActivity;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.display.DisplayCommunity;
import com.trutek.looped.ui.communityDashboard.myCommunities.display.MembersAdapter;
import com.trutek.looped.ui.customcontrol.CustomEditText;
import com.trutek.looped.ui.profile.display.DisplayProfile;
import com.trutek.looped.utils.DialogUtil;
import com.trutek.looped.utils.KeyboardUtils;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class DisplayActivity extends BaseAppCompatActivity {

    static final String TAG = DisplayActivity.class.getSimpleName();
    public static final int EDIT_ACTIVITY = 1;

    @Inject
    IActivityService activityService;
    @Inject
    ICommentService commentService;

    @Inject
    IProfileService _ProfileService;

    @Inject
    IReportBugService _ReportBugService;

    private static final int REQUEST_ACTIVITY_DISCUSSION = 2;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.text_activity_header)
    TextView header;
    @BindView(R.id.image_view_activity)
    MaskedImageView activityImage;
    @BindView(R.id.txt_community_name)
    TextView communityName;
    @BindView(R.id.activity_name)
    TextView activityName;
    @BindView(R.id.activity_date)
    TextView activityDate;
    @BindView(R.id.txt_join)
    TextView textJoin;
    //    @BindView(R.id.text_at) TextView textAt;
    @BindView(R.id.activity_time)
    TextView activityTime;
    @BindView(R.id.activity_address)
    TextView activityAddress;
    @BindView(R.id.activity_hosted_by)
    TextView hostedBy;
    @BindView(R.id.hosted_by_name)
    TextView hostedByName;
    @BindView(R.id.txt_activity_description)
    TextView activityDescription;
    @BindView(R.id.recycler_view_activity_member)
    RecyclerView recyclerViewMember;


    TextView textView_discussion, textView_discussionAll,textView_discussion_default;
    CustomEditText editText_addComment;
    ProgressBar progressBar_comments;

    RecyclerView recyclerView_comments;

    LinearLayout linearLayout_discussion;


    private ActivityModel activity;
    private ArrayList<MemberModel> members;
    private MembersAdapter membersAdapter;

    private ArrayList<CommentModel> comments;
    ArrayList<ArrayList<CommentModel>> subComments;
    private DiscussionAdapter discussionAdapter;
    private Tracker mTracker;
    MenuItem menuItem_edit;
    private boolean isReply;

    int replyToActivity = -1;


    public static void start(Context context, ActivityModel activity) {
        Intent intent = new Intent(context, DisplayActivity.class);
        intent.putExtra("activityModel", activity);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_display;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initFields();

        getActivity();
        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(Constants.Activity_SCREEN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        listeners();
    }

    private void initFields() {
        activity = (ActivityModel) getIntent().getSerializableExtra("activityModel");
        editText_addComment = (CustomEditText) findViewById(R.id.edit_text_discussion_activity);
        textView_discussion = (TextView) findViewById(R.id.discussion_activity_textView_discussion_title);
        textView_discussionAll = (TextView) findViewById(R.id.discussion_activity_textView_show_discussion);
        textView_discussion_default = (TextView) findViewById(R.id.content_discussion_textView_default);


        recyclerView_comments = (RecyclerView) findViewById(R.id.recycler_discussion_activity);
        progressBar_comments = (ProgressBar) findViewById(R.id.content_discussion_progressBar);
        linearLayout_discussion = (LinearLayout) findViewById(R.id.content_display_activity_linearLayout_discussion);

        setHeader();
        initAdapters();
        setFonts();
    }

    private void listeners() {
        editText_addComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (!isReply) {
                        createComment();
                    } else {
                        replyComment(editText_addComment.getText().toString());
                    }

                    KeyboardUtils.hideKeyboard(editText_addComment);
                    editText_addComment.setHint(getString(R.string.comment_text_hint));
                    editText_addComment.setText("");
                    isReply = false;
                    return true;
                }
                return false;
            }
        });

        textView_discussionAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Start the discussion")
                        .setAction("Display Activity")
                        .build());
                Intent intentCommunity = new Intent(DisplayActivity.this, DiscussionActivity.class);
                intentCommunity.putExtra(Constants.MODEL_ACTIVITY, activity);
                startActivityForResult(intentCommunity, REQUEST_ACTIVITY_DISCUSSION);
            }
        });
    }

    private void setFonts() {
        Typeface boldFont = Typeface.createFromAsset(getAssets(), Constants.AvenirNextBold);
        Typeface nextRegular = Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        communityName.setTypeface(nextRegular);
        activityName.setTypeface(boldFont);
        activityDate.setTypeface(nextRegular);
//        textAt.setTypeface(nextRegular);
        activityTime.setTypeface(nextRegular);
        activityAddress.setTypeface(nextRegular);
        hostedBy.setTypeface(nextRegular);
        hostedByName.setTypeface(nextRegular);
        activityDescription.setTypeface(nextRegular);
        textView_discussionAll.setTypeface(avenirNextRegular);
        textView_discussion.setTypeface(avenirNextRegular);
    }

    private void setHeader() {
        header.setText(activity.subject);
    }

    private void getActivity() {
        if (activity == null) {
            return;
        }
        showProgress();
        activityService.getActivity(activity, new AsyncResult<ActivityModel>() {
            @Override
            public void success(final ActivityModel activityModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setActivityData(activityModel);
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

    private void setActivityData(ActivityModel activityModel) {

        if (activityModel.picUrl != null && !activityModel.picUrl.isEmpty()) {
            displayImageByUrl(activityModel.picUrl, activityImage);
        } else {
           /* Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.default_camera);
            activityImage.setImageBitmap(image);*/
            displayImageByUrl("drawable://"+ R.drawable.default_camera, activityImage);
        }

        //activity member
        members.addAll(activityModel.participants);
        membersAdapter.notifyDataSetChanged();

        // resolve join joined
        String profileId = _ProfileService.getMyProfile(null).getServerId();
        for (MemberModel member : members) {
            if (member.profile.getServerId().equalsIgnoreCase(profileId)) {
                activity.isSelected = true;
            }
        }

        if (activity.isSelected) {
            textJoin.setText(getString(R.string.text_joined));

        } else {
            textJoin.setText(getString(R.string.text_join));
        }

        Log.d(TAG, "MyActivity: " + activity.isMine);

        if (null != activityModel.getAdmin() && activityModel.getAdmin().getServerId().equals(profileId)) {
            menuItem_edit.setVisible(true);
        } else {
            menuItem_edit.setVisible(false);
        }


        communityName.setText(activityModel.community.subject);

        activityName.setText(activityModel.subject);
        activityDate.setText(DateHelper.stringify(activityModel.dueDate, DateHelper.StringifyAs.FullMonthDate));
        activityTime.setText(DateHelper.stringifyTime(activityModel.dueDate));
        activityAddress.setText(activityModel.location.name);
        if (activity.admin != null && activity.admin.name != null)
            hostedByName.setText(activity.admin.name);

        activityDescription.setText(activityModel.body);

        showDiscussionLayout(activity.isSelected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        membersAdapter.notifyDataSetChanged();
    }

    private void displayImageByUrl(String publicUrl, MaskedImageView imageView) {
        ImageLoader.getInstance().displayImage(publicUrl, imageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    private void initAdapters() {
        //member adapter
        members = new ArrayList<>();
        membersAdapter = new MembersAdapter(this, members, asyncResult_profile);

        final LinearLayoutManager layoutManagerMembers = new LinearLayoutManager(this);
        layoutManagerMembers.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerViewMember.setLayoutManager(layoutManagerMembers);
        recyclerViewMember.setAdapter(membersAdapter);

        //discussion Adapter
        comments = new ArrayList<>();
        subComments = new ArrayList<>();

        discussionAdapter = new DiscussionAdapter(comments, subComments, interestSelectedActionListeners,
                replyListener,
                subCommentListener,
                commentOptionListener, subCommentOptionListener);

        LinearLayoutManager layoutManagerDiscussion = new LinearLayoutManager(this);

        recyclerView_comments.setLayoutManager(layoutManagerDiscussion);
        recyclerView_comments.setAdapter(discussionAdapter);
    }

    OnActionListener<CommentModel> interestSelectedActionListeners = new OnActionListener<CommentModel>() {
        @Override
        public void notify(CommentModel commentModel) {
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

    AsyncResult<MemberModel> asyncResult_profile = new AsyncResult<MemberModel>() {
        @Override
        public void success(MemberModel memberModelModel) {

        }

        @Override
        public void error(String error) {

        }
    };

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit_icon_layout, menu);
        menuItem_edit = menu.findItem(R.id.edit_marker);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.edit_marker:
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("activityModel", activity);
                startActivityForResult(intent, EDIT_ACTIVITY);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_ACTIVITY && resultCode == RESULT_OK) {

            activity = (ActivityModel) data.getSerializableExtra("activityModel");
            if (activity.participants != null) {
                activity.participants.clear();
            }
            setActivityData(activity);
        }else if(requestCode == REQUEST_ACTIVITY_DISCUSSION && resultCode == RESULT_OK){
            if(null != data){
                comments.clear();
                subComments.clear();
                ArrayList<CommentModel> models = (ArrayList<CommentModel>) data.getSerializableExtra(Constants.INTENT_KEY_COMMENTS);
                ArrayList<ArrayList<CommentModel>> subModels = (ArrayList<ArrayList<CommentModel>>)data.getSerializableExtra(Constants.INTENT_KEY_SUB_COMMENTS);
                comments.addAll(models);
                subComments.addAll(subModels);
                discussionAdapter.notifyDataSetChanged();
            }
        }
    }


    /*@OnClick(R.id.txt_show_all)
    public void viewAll() {
        Intent intent = new Intent(this, DiscussionActivity.class);
        intent.putExtra("activityModel", activity);
        startActivity(intent);
    }*/

    public void getDiscussion() {
        showCommentProgress();
        commentService.allComments(activity.getServerId(), new AsyncResult<Page<CommentModel>>() {
            @Override
            public void success(final Page<CommentModel> commentPage) {

                Collections.sort(commentPage.items, new Comparator<CommentModel>() {
                    @Override
                    public int compare(CommentModel o1, CommentModel o2) {
                        return o2.date.compareTo(o1.date);
                    }
                });

                int maxIndex = commentPage.items.size() > 3 ? 3 : commentPage.items.size();

                for (int i = 0; i < maxIndex; i++) {
                    comments.add(commentPage.items.get(i));
                }

                for (int i = 0; i < comments.size(); i++) {
                    subComments.add(new ArrayList<CommentModel>());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {








                       /* boolean showAll = false;
                        boolean startDiscussion = false;

                        if(commentPage.items.size() > 3){
                            showAll = true;
                            startDiscussion = false;
                            for (int i = 0 ; i <= 2 ; i++){
                                comments.add(commentPage.items.get(i));
                            }
                        } else if(commentPage.items.size() == 3){
                            for (int i = 0 ; i <= 2 ; i++){
                                comments.add(commentPage.items.get(i));
                            }
                        } else if (commentPage.items.size() == 0){
                            showAll = false;
                            startDiscussion = true;
                        } else {
                            comments.addAll(commentPage.items);
                            showAll = true;
                            startDiscussion = false;
                        }*/


                        discussionAdapter.notifyDataSetChanged();
                        hideCommentProgress();
//                        resolveDiscussionCard(showAll, startDiscussion);

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

    @OnClick(R.id.txt_join)
    public void join() {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Joined the Activity")
                .setAction("Display Activity")
                .build());
        if (activity.isSelected) {
            return;
        }

        activityService.joinActivity(activity, new AsyncResult<ActivityModel>() {
            @Override
            public void success(ActivityModel activityModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.setSelected(true);
                        textJoin.setText(getString(R.string.text_joined));
                        showDiscussionLayout(activity.isSelected);
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

    private void resolveDiscussionCard(boolean showAllBoolean, boolean startDiscussionBoolean) {

        /*if (startDiscussionBoolean) {
            startDiscussion.setVisibility(View.VISIBLE);
            showAll.setVisibility(View.GONE);
        } else if (showAllBoolean) {
            startDiscussion.setVisibility(View.GONE);
            showAll.setVisibility(View.VISIBLE);
        } else {
            startDiscussion.setVisibility(View.GONE);
            showAll.setVisibility(View.VISIBLE);
        }*/
    }

    OnActionListener<Integer> commentOptionListener = new OnActionListener<Integer>() {
        @Override
        public void notify(final Integer position) {
            ArrayList<Integer> hideOptions = new ArrayList<>();
            hideOptions.add(DialogUtil.OPTION_UNFRIEND);
            if (comments.get(position).getProfile().getServerId().equals(_ProfileService.getMyProfile(null).getServerId())) {
                hideOptions.add(DialogUtil.OPTION_REPORT_ABUSE);
            } else {
                hideOptions.add(DialogUtil.OPTION_DELETE);
            }
            DialogUtil.showOptionsDialog(DisplayActivity.this, new AsyncResult<Integer>() {
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

    private void createComment() {
        final CommentModel comment = new CommentModel();
        comment.text = editText_addComment.getText().toString();
        comment.activity = activity;
        comment.date = new Date();

        commentService.createComment(comment, new AsyncResult<CommentModel>() {
            @Override
            public void success(final CommentModel comment) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(comments.size()>=3) {
                            comments.remove(2);
                        }
                        comments.add(0, comment);

                        discussionAdapter.notifyDataSetChanged();
                        editText_addComment.setText("");
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
        reportAbuse.setName(activity.getSubject());
        showProgress();
        _ReportBugService.reportBug(reportAbuse, asyncResult_reportAbuse);
    }

    void reportAbuse(NestedRecyclerViewModel nestedRecyclerViewModel) {
        ReportBugModel reportAbuse = new ReportBugModel();
        reportAbuse.setServerId(subComments.get(nestedRecyclerViewModel.getParentPosition()).get(nestedRecyclerViewModel.getChildPosition()).getServerId());
        reportAbuse.setAbuseFor("Comment");
        reportAbuse.setName(activity.getSubject());
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

    private void replyComment(String text) {
        CommentModel commentModel = new CommentModel();
        commentModel.text = text;
        commentService.createSubComment(commentModel, comments.get(replyToActivity).getServerId(), new AsyncResult<CommentModel>() {
            @Override
            public void success(CommentModel commentModel) {
                subComments.get(replyToActivity).add(commentModel);
                comments.get(replyToActivity).setThreadCount(comments.get(replyToActivity).getThreadCount() + 1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        discussionAdapter.notifyItemChanged(replyToActivity);
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

    OnActionListener<Integer> replyListener = new OnActionListener<Integer>() {
        @Override
        public void notify(Integer position) {
            editText_addComment.setHint("Reply to " + comments.get(position).getProfile().getName());
            editText_addComment.requestFocus();
            KeyboardUtils.showKeyboard(editText_addComment);
            isReply = true;
            replyToActivity = position;
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

    OnActionListener<NestedRecyclerViewModel> subCommentOptionListener = new OnActionListener<NestedRecyclerViewModel>() {
        @Override
        public void notify(final NestedRecyclerViewModel nestedRecyclerViewModel) {
            ArrayList<Integer> hideOptions = new ArrayList<>();
            hideOptions.add(DialogUtil.OPTION_UNFRIEND);

            if (subComments.get(nestedRecyclerViewModel.getParentPosition()).get(nestedRecyclerViewModel.getChildPosition()).getProfile().getServerId().equals(_ProfileService.getMyProfile(null).getServerId())) {
                hideOptions.add(DialogUtil.OPTION_REPORT_ABUSE);
            } else {
                hideOptions.add(DialogUtil.OPTION_DELETE);
            }


            DialogUtil.showOptionsDialog(DisplayActivity.this, new AsyncResult<Integer>() {
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

    void showDiscussionLayout(boolean showDiscussion){
        if(showDiscussion){
            linearLayout_discussion.setVisibility(View.VISIBLE);
            getDiscussion();
        }else {
            linearLayout_discussion.setVisibility(View.GONE);
        }
    }




}
