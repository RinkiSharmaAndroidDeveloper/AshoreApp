package com.trutek.looped.ui.communityDashboard.publiccommunity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.LocationModel;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.data.impl.entities.Community;
import com.trutek.looped.msas.common.contracts.AsyncNotify;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.ui.authenticate.SignUpActivity;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.communityDashboard.myCommunities.display.DisplayCommunity;
import com.trutek.looped.ui.profile.create.CreateProfileActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PublicCommunityActivity extends BaseAppCompatActivity implements View.OnClickListener {

    static final String TAG = PublicCommunityActivity.class.getSimpleName();

    @Inject
    ICommunityService _ICommunityService;

    @Inject
    IProfileService _ProfileService;

    ImageView imageView_back;
    TextView textView_title, textView_next, textView_defaultText;
    RecyclerView mRecyclerView;

    List<CommunityModel> mCommunityModels;
    List<CommunityModel> filterCommunityModels;
    PublicCommunityAdapter mAdapter;
    EditText editText_search;
    String profileId;

    @Override
    protected int getContentResId() {
        return R.layout.activity_public_community;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileId = _ProfileService.getMyProfile(null).getServerId();
        initViews();
        setFonts();
        listener();
        setAdapter();
        getCommunities();
        textView_next.setOnClickListener(this);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void initViews() {

        imageView_back = (ImageView) findViewById(R.id.pca_imageView_back);
        textView_title = (TextView) findViewById(R.id.pca_textView_title);
        textView_next = (TextView) findViewById(R.id.pca_textView_next);
        textView_defaultText = (TextView) findViewById(R.id.public_community_default_text);
        mRecyclerView = (RecyclerView) findViewById(R.id.pca_recyclerView);
        editText_search = (EditText) findViewById(R.id.pca_editText_search);
        mCommunityModels = new ArrayList<>();
        filterCommunityModels = new ArrayList<>();

    }

    void setEmptyText(RecyclerView recyclerView, TextView textView_noFound, boolean anyRecordFound) {
        if (anyRecordFound) {
            recyclerView.setVisibility(View.VISIBLE);
            textView_noFound.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            textView_noFound.setVisibility(View.VISIBLE);
        }
    }

    private void setFonts() {

        textView_title.setTypeface(avenirNextRegular);
        textView_next.setTypeface(avenirNextRegular);
        editText_search.setTypeface(avenirNextRegular);

    }

    void setAdapter() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new PublicCommunityAdapter(mCommunityModels
                , filterCommunityModels
                , asyncResult_communityJoining
                , asyncResult_openCommunity);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        setEmptyText(mRecyclerView, textView_defaultText, mAdapter.getItemCount() != 0);

    }

    private void listener() {
        imageView_back.setOnClickListener(this);
        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //mAdapter.getFilter().filter(s.toString());
                if (filterCommunityModels != null) {
                    filterCommunityModels.clear();
                }

                for (CommunityModel communityModel : mCommunityModels) {
                    if (communityModel.subject != null && communityModel.subject.toLowerCase().startsWith(s.toString().toLowerCase())) {
                        filterCommunityModels.add(communityModel);
                    }
                }
                mAdapter.notifyDataSetChanged();
                setEmptyText(mRecyclerView, textView_defaultText, mAdapter.getItemCount() != 0);
            }
        });
    }

    void getCommunities() {
        _ICommunityService.fetchCommunities(pageAsyncResult_getCommunity);
    }

    AsyncResult<Page<CommunityModel>> pageAsyncResult_getCommunity = new AsyncResult<Page<CommunityModel>>() {
        @Override
        public void success(final Page<CommunityModel> communityModelPage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCommunityModels.clear();
                    filterCommunityModels.clear();
                    mCommunityModels.addAll(communityModelPage.items);
                    filterCommunityModels.addAll(mCommunityModels);
                    if (null != mAdapter) {
                        mAdapter.notifyDataSetChanged();
                        setEmptyText(mRecyclerView, textView_defaultText, mAdapter.getItemCount() != 0);
                    } else {
                        Log.e(TAG, "pageAsyncResult_getCommunity: adapter is null");
                    }
                }
            });

        }

        @Override
        public void error(String error) {

        }
    };

    AsyncResult<Integer> asyncResult_communityJoining = new AsyncResult<Integer>() {
        @Override
        public void success(final Integer position) {
            showProgress();

            if (!mCommunityModels.get(position).isSelected) {
                joinCommunity(position);
            } else {
                leaveCommunity(position);
            }

        }

        @Override
        public void error(String error) {

        }
    };

    void joinCommunity(final int position) {
        CommunityModel community = new CommunityModel();
        community = mCommunityModels.get(position);
        community.profileIds.add(profileId);
        _ICommunityService.joinCommunity(community, new AsyncResult<CommunityModel>() {
            @Override
            public void success(CommunityModel communityModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mAdapter) {
                            mCommunityModels.get(position).isSelected = true;
                            mAdapter.notifyItemChanged(position);
                        } else {
                            Log.e(TAG, "asyncResult_communityJoining: adapter is null");
                        }
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

    void leaveCommunity(final int position) {
        _ICommunityService.leaveCommunity(profileId, mCommunityModels.get(position), new AsyncNotify() {
            @Override
            public void success() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mAdapter) {
                            mCommunityModels.get(position).isSelected = false;
                            mAdapter.notifyItemChanged(position);
                        } else {
                            Log.e(TAG, "asyncResult_communityJoining: adapter is null");
                        }
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
            finish();
        }
        if (view.getId() == textView_next.getId()) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }
    }

    AsyncResult<CommunityModel> asyncResult_openCommunity = new AsyncResult<CommunityModel>() {
        @Override
        public void success(CommunityModel communityModel) {
            Intent intent = new Intent(PublicCommunityActivity.this, DisplayCommunity.class);
            intent.putExtra("communityModel", communityModel);
            intent.putExtra("OPEN_FROM", 0);
            startActivityForResult(intent,1);
        }

        @Override
        public void error(String error) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if (!mCommunityModels.get(mAdapter.getClickedItemPosition()).isSelected) {
                joinCommunity(mAdapter.getClickedItemPosition());
            } else {
                leaveCommunity(mAdapter.getClickedItemPosition());
            }
        }
    }
}
