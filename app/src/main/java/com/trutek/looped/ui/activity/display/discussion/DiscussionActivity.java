package com.trutek.looped.ui.activity.display.discussion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ActivityModel;
import com.trutek.looped.data.contracts.models.CommentModel;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.NestedRecyclerViewModel;
import com.trutek.looped.data.contracts.models.ReportBugModel;
import com.trutek.looped.data.contracts.services.ICommentService;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.data.contracts.services.IReportBugService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.display.DisplayCommunity;
import com.trutek.looped.utils.DialogUtil;
import com.trutek.looped.utils.KeyboardUtils;
import com.trutek.looped.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class DiscussionActivity extends BaseAppCompatActivity implements View.OnClickListener {

    @Inject
    ICommentService commentService;

    @Inject
    IProfileService _ProfileService;

    @Inject
    IReportBugService _ReportBugService;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.image_edit_discussion_activity)
    ImageView imv_description;
    @BindView(R.id.edit_text_discussion_activity)
    EditText edit_txt_description;
    @BindView(R.id.content_description_button_reply)
    Button button_reply;
    @BindView(R.id.recycler_discussion_activity)
    RecyclerView recyclerView;
    // @BindView(R.id.txt_display_discussion_activity) TextView txt_heading;

    LinearLayout linearLayout_heading, linearLayout_option1, linearLayout_option2;
    ImageView imageView_back;
    TextView textView_title;
    EditText editText_commentOption1;

    private ArrayList<CommentModel> comments;
    ArrayList<ArrayList<CommentModel>> subComments;
    private DiscussionAdapter discussionAdapter;
    private ActivityModel activity;
    CommunityModel communityModel;

    boolean isReply = false;
    int replyToComment = -1;

    @Override
    protected int getContentResId() {
        return R.layout.activity_discussion;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initFields();
        setFonts();
        listeners();
    }

    private void initFields() {
        activity = (ActivityModel) getIntent().getSerializableExtra("activityModel");
        communityModel = (CommunityModel) getIntent().getSerializableExtra(Constants.MODEL_COMMUNITY);
        comments = new ArrayList<>();
        subComments = new ArrayList<>();

        linearLayout_heading = (LinearLayout) findViewById(R.id.content_discussion_linearLayout_heading);
        linearLayout_heading.setVisibility(View.GONE);

        linearLayout_option1 = (LinearLayout) findViewById(R.id.content_discussion_linearLayout_addComment_option1);
        linearLayout_option2 = (LinearLayout) findViewById(R.id.content_discussion_linearLayout_addComment_option2);

        linearLayout_option1.setVisibility(View.VISIBLE);
        linearLayout_option2.setVisibility(View.GONE);
        button_reply.setVisibility(View.VISIBLE);

        editText_commentOption1 = (EditText) findViewById(R.id.edit_text_discussion_activity_option1);

        imageView_back = (ImageView) findViewById(R.id.discussion_activity_imageView_back);

        textView_title = (TextView) findViewById(R.id.discussion_activity_textView_title);

        initAdapter();
        if (null != activity) {
            allComments();
        }

        if (null != communityModel) {
            getCommunityComments();
        }
    }

    private void setFonts() {
        textView_title.setTypeface(avenirNextRegular);
    }

    private void listeners() {
        imageView_back.setOnClickListener(this);

        editText_commentOption1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    commentAction();
                    return true;
                }
                return false;
            }
        });
    }

    public void initAdapter() {
        discussionAdapter = new DiscussionAdapter(comments, subComments, interestSelectedActionListeners,
                replyListener, subCommentListener, commentOptionListener, subCommentOptionListener);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(discussionAdapter);
    }

    void setButtonText() {
        if (isReply) {
            button_reply.setText(getString(R.string.reply));
        } else {
            button_reply.setText(getString(R.string.discussion_activity_text_comment));
        }
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_KEY_COMMENTS,comments);
        intent.putExtra(Constants.INTENT_KEY_SUB_COMMENTS, subComments);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.content_description_button_reply)
    public void reply() {
        commentAction();
    }

    private void createComment() {
        final CommentModel comment = new CommentModel();
        comment.text = editText_commentOption1.getText().toString();
        if (null != activity) {
            comment.activity = activity;
        } else if (null != communityModel) {
            comment.setCommunity(communityModel);
        } else {
            return;
        }
        comment.date = new Date();

        commentService.createComment(comment, new AsyncResult<CommentModel>() {
            @Override
            public void success(final CommentModel comment) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        comments.add(0, comment);
                        discussionAdapter.notifyDataSetChanged();
                        editText_commentOption1.setText("");
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

    private void allComments() {

        showProgress();
        commentService.allComments(activity.getServerId(), asyncResult_commentsList);
    }

    void getCommunityComments() {
        showProgress();
        commentService.allCommunityComments(communityModel.getServerId(), asyncResult_commentsList);
    }

    AsyncResult<Page<CommentModel>> asyncResult_commentsList = new AsyncResult<Page<CommentModel>>() {
        @Override
        public void success(final Page<CommentModel> commentModelPage) {

            comments.addAll(commentModelPage.items);

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
    };

    OnActionListener<Integer> replyListener = new OnActionListener<Integer>() {
        @Override
        public void notify(Integer position) {
            editText_commentOption1.setHint("Reply to " + comments.get(position).getProfile().getName());
            editText_commentOption1.requestFocus();
            KeyboardUtils.showKeyboard(editText_commentOption1);
            isReply = true;
            setButtonText();
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

    void replyComment(String text) {
        CommentModel commentModel = new CommentModel();
        commentModel.text = text;
        showProgress();
        commentService.createSubComment(commentModel, comments.get(replyToComment).getServerId(), new AsyncResult<CommentModel>() {
            @Override
            public void success(CommentModel commentModel) {
                subComments.get(replyToComment).add(0,commentModel);
                comments.get(replyToComment).setThreadCount(comments.get(replyToComment).getThreadCount() + 1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        discussionAdapter.notifyItemChanged(replyToComment);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == imageView_back.getId()) {
            Intent intent = new Intent();
            intent.putExtra(Constants.INTENT_KEY_COMMENTS,comments);
            intent.putExtra(Constants.INTENT_KEY_SUB_COMMENTS, subComments);
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    void commentAction() {
        if (!isReply) {
            createComment();
        } else {
            replyComment(editText_commentOption1.getText().toString());
        }

        KeyboardUtils.hideKeyboard(editText_commentOption1);
        editText_commentOption1.setHint(getString(R.string.comment_text_hint));
        editText_commentOption1.setText("");
        isReply = false;
        setButtonText();
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
            DialogUtil.showOptionsDialog(DiscussionActivity.this, new AsyncResult<Integer>() {
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
        if (null != communityModel) {
            reportAbuse.setAbuseFor("Comment");
            reportAbuse.setName(communityModel.getSubject());
        } else if (null != activity) {
            reportAbuse.setAbuseFor("Activity");
            reportAbuse.setName(activity.getSubject());
        }

        showProgress();
        _ReportBugService.reportBug(reportAbuse, asyncResult_reportAbuse);
    }

    void reportAbuse(NestedRecyclerViewModel nestedRecyclerViewModel){
        ReportBugModel reportAbuse = new ReportBugModel();
        reportAbuse.setServerId(subComments.get(nestedRecyclerViewModel.getParentPosition())
                .get(nestedRecyclerViewModel.getChildPosition()).getServerId());
        reportAbuse.setAbuseFor("Comment");
        reportAbuse.setName(communityModel.getSubject());
        showProgress();
        _ReportBugService.reportBug(reportAbuse,asyncResult_reportAbuse);
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


            DialogUtil.showOptionsDialog(DiscussionActivity.this, new AsyncResult<Integer>() {
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

                                int cnt = comments.get(nestedRecyclerViewModel.getParentPosition()).getThreadCount();
                                comments.get(nestedRecyclerViewModel.getParentPosition()).setThreadCount(cnt - 1);

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

    @Override
    protected void onPause() {
        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_KEY_COMMENTS,comments);
        intent.putExtra(Constants.INTENT_KEY_SUB_COMMENTS, subComments);
        setResult(RESULT_OK,intent);
        super.onPause();

    }
}
