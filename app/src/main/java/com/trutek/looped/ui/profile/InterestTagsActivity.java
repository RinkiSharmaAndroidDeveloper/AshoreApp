package com.trutek.looped.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.FilterModel;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.contracts.services.IInterestService;
import com.trutek.looped.data.contracts.services.ITagService;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.profile.create.adapter.InterestAdapter;
import com.trutek.looped.ui.profile.create.adapter.TagAdapter;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.listeners.EndlessScrollListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class InterestTagsActivity extends BaseAppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    public class OpenForm {
        public static final int EDIT_PROFILE = 0;
        public static final int DISCOVER_PROFILE = 1;
        public static final int DISCOVER_ACTIVITY = 2;
        public static final int CONNECTION_ACTIVITY = 3;
    }

    @Inject
    ITagService tagService;


    @Inject
    IInterestService interestService;

    @BindView(R.id.edit_text_topic_search)
    EditText tag_search;
    @BindView(R.id.edit_text_interest_search) EditText interest_search;
    @BindView(R.id.recycler_view_topic)
    RecyclerView recyclerViewTopic;
    @BindView(R.id.recycler_view_interest) RecyclerView recyclerViewInterest;

    private ProfileModel profile;
    private FilterModel filter;
    private EndlessScrollListener scrollListenerTag;
    private EndlessScrollListener scrollListenerInterest;

    private OnActionListener<TagModel> tagSelectedActionListeners;
    private OnActionListener<TagModel> tagUnSelectedActionListeners;
    private OnActionListener<InterestModel> interestSelectedActionListeners;
    private OnActionListener<InterestModel> interestUnSelectedActionListeners;

    private TagAdapter tagAdapter;
    private InterestAdapter interestAdapter;

    private ArrayList<TagModel> tagList;
    private HashMap<String, TagModel> mapTags;
    private ArrayList<InterestModel> interestsList;
    private HashMap<String, InterestModel> mapInterests;

    private ArrayList<TagModel> filteredTags;
    private ArrayList<InterestModel> filteredInterests;

    PageInput tagInput;
    PageInput interestsInput;

    private int OPEN_FORM;

    @Override
    protected int getContentResId() {
        return R.layout.activity_interest_tags;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initFields();
    }



    public void initFields(){
        init();
        initListeners();

        initializeInterests();
        initializeTags();

        initAdapters();
    }

    private void init(){
        if (getIntent().getIntExtra("OPEN_FORM", 0) == OpenForm.EDIT_PROFILE) {
            OPEN_FORM = OpenForm.EDIT_PROFILE;
            profile = (ProfileModel) getIntent().getSerializableExtra("profileModel");
        }
        if (getIntent().getIntExtra("OPEN_FORM", 0) == OpenForm.DISCOVER_ACTIVITY) {
            OPEN_FORM = OpenForm.DISCOVER_ACTIVITY;
            filter = (FilterModel) getIntent().getSerializableExtra("filterModel");
        }
        if (getIntent().getIntExtra("OPEN_FORM", 0) == OpenForm.DISCOVER_PROFILE) {
            OPEN_FORM = OpenForm.DISCOVER_PROFILE;
            filter = (FilterModel) getIntent().getSerializableExtra("filterModel");
        }
        if (getIntent().getIntExtra("OPEN_FORM", 0) == OpenForm.CONNECTION_ACTIVITY) {
            OPEN_FORM = OpenForm.CONNECTION_ACTIVITY;
            filter = (FilterModel) getIntent().getSerializableExtra("filterModel");
        }

        tagSelectedActionListeners = new OnActionListener<TagModel>() {
            @Override
            public void notify(TagModel tagModel) {
                if(OPEN_FORM == OpenForm.EDIT_PROFILE){
                    profile.tags.add(tagModel);
                }
                if(OPEN_FORM == OpenForm.DISCOVER_ACTIVITY || OPEN_FORM == OpenForm.DISCOVER_PROFILE || OPEN_FORM == OpenForm.CONNECTION_ACTIVITY){
                    filter.tags.add(tagModel);
                }
            }
        };

        tagUnSelectedActionListeners = new OnActionListener<TagModel>() {
            @Override
            public void notify(TagModel tagModel) {
                if(OPEN_FORM == OpenForm.EDIT_PROFILE){
                    profile.tags.remove(tagModel);
                }
                if(OPEN_FORM == OpenForm.DISCOVER_ACTIVITY || OPEN_FORM == OpenForm.DISCOVER_PROFILE || OPEN_FORM == OpenForm.CONNECTION_ACTIVITY){
                    filter.tags.remove(tagModel);
                }

            }
        };

        interestSelectedActionListeners  = new OnActionListener<InterestModel>() {
            @Override
            public void notify(InterestModel interestModel) {
                if(OPEN_FORM == OpenForm.EDIT_PROFILE){
                    profile.interests.add(interestModel);
                }
                if(OPEN_FORM == OpenForm.DISCOVER_ACTIVITY || OPEN_FORM == OpenForm.DISCOVER_PROFILE || OPEN_FORM == OpenForm.CONNECTION_ACTIVITY){
                    filter.interests.add(interestModel);
                }

            }
        };

        interestUnSelectedActionListeners  = new OnActionListener<InterestModel>() {
            @Override
            public void notify(InterestModel interestModel) {
                if(OPEN_FORM == OpenForm.EDIT_PROFILE){
                    profile.interests.remove(interestModel);
                }
                if(OPEN_FORM == OpenForm.DISCOVER_ACTIVITY || OPEN_FORM == OpenForm.DISCOVER_PROFILE || OPEN_FORM == OpenForm.CONNECTION_ACTIVITY){
                    filter.interests.remove(interestModel);
                }
            }
        };

        tagInput = new PageInput();
        interestsInput = new PageInput();

        tagList = new ArrayList<>();
        interestsList = new ArrayList<>();

        filteredTags = new ArrayList<>();
        filteredInterests = new ArrayList<>();

        mapTags = new HashMap<>();
        mapInterests = new HashMap<>();

        tagAdapter = new TagAdapter(tagList, filteredTags, tagSelectedActionListeners, tagUnSelectedActionListeners);
        interestAdapter = new InterestAdapter(interestsList, filteredInterests, interestSelectedActionListeners, interestUnSelectedActionListeners);
    }

    private void initListeners() {
        tag_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tagAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        interest_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                interestAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initAdapters() {
//        recyclerViewTopic.addOnScrollListener(scrollListenerTag);
//        recyclerViewInterest.addOnScrollListener(scrollListenerInterest);

        final LinearLayoutManager layoutManagerTopic = new LinearLayoutManager(this);
        final LinearLayoutManager layoutManagerInterest = new LinearLayoutManager(this);

        recyclerViewTopic.setLayoutManager(layoutManagerTopic);
        recyclerViewInterest.setLayoutManager(layoutManagerInterest);

        recyclerViewTopic.setAdapter(tagAdapter);
        recyclerViewInterest.setAdapter(interestAdapter);
    }

    private void initializeTags() {

        tagInput.pageNo = 1;
        if (scrollListenerTag != null) {
            scrollListenerTag.reset();
        }

        tagList.clear();
        mapTags.clear();

        if(OPEN_FORM == OpenForm.EDIT_PROFILE){
            List<TagModel> selectedInterests = profile.tags;
            for(TagModel item : selectedInterests){
                item.isSelected = true;
                mapTags.put(item.getName(), item);
            }
        }

        if(OPEN_FORM == OpenForm.DISCOVER_PROFILE || OPEN_FORM == OpenForm.DISCOVER_ACTIVITY || OPEN_FORM == OpenForm.CONNECTION_ACTIVITY){
            List<TagModel> selectedInterests = filter.tags;
            for(TagModel item : selectedInterests){
                item.isSelected = true;
                mapTags.put(item.getName(), item);
            }
        }

        loadTopics();
    }

    private void initializeInterests() {

        interestsInput.pageNo = 1;
        if (scrollListenerInterest != null) {
            scrollListenerInterest.reset();
        }

        interestsList.clear();
        mapInterests.clear();

        if(OPEN_FORM == OpenForm.EDIT_PROFILE){
            List<InterestModel> selectedInterests = profile.interests;
            for(InterestModel item : selectedInterests){
                item.isSelected = true;
                mapInterests.put(item.getName(), item);
            }
        }

        if(OPEN_FORM == OpenForm.DISCOVER_PROFILE || OPEN_FORM == OpenForm.DISCOVER_ACTIVITY || OPEN_FORM == OpenForm.CONNECTION_ACTIVITY){
            ArrayList<InterestModel> selectedInterests = filter.interests;
            for(InterestModel item : selectedInterests){
                item.isSelected = true;
                mapInterests.put(item.getName(), item);
            }
        }

        loadInterests();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void loadTopics() {
        tagService.getAll(tagInput, new AsyncResult<Page<TagModel>>() {
            @Override
            public void success(Page<TagModel> models) {

                for (TagModel item : models.items) {
                    if (!mapTags.containsKey(item.getName())){
                        mapTags.put(item.getName(), item);
                    }
                }

                tagList.addAll(mapTags.values());

                Collections.sort(tagList, new Comparator<TagModel>() {
                    @Override
                    public int compare(TagModel lhs, TagModel rhs) {
                        if (lhs.isSelected && !rhs.isSelected)
                            return -1;

                        if (lhs.isSelected && rhs.isSelected)
                            return 0;

                        if (!lhs.isSelected && rhs.isSelected)
                            return 1;

                        return 0;
                    }
                });

                filteredTags.addAll(tagList);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tagAdapter.setModified();
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

    private void loadInterests() {
        interestService.getAll(interestsInput, new AsyncResult<Page<InterestModel>>() {
            @Override
            public void success(Page<InterestModel> models) {

                for (InterestModel item : models.items) {
                    if(!mapInterests.containsKey(item.getName())){
                        mapInterests.put(item.getName(), item);
                    }
                }

                interestsList.addAll(mapInterests.values());

                Collections.sort(interestsList, new Comparator<InterestModel>() {
                    @Override
                    public int compare(InterestModel lhs, InterestModel rhs) {
                        if (lhs.isSelected && !rhs.isSelected)
                            return -1;

                        if (lhs.isSelected && rhs.isSelected)
                            return 0;

                        if (!lhs.isSelected && rhs.isSelected)
                            return 1;

                        return 0;
                    }
                });

                filteredInterests.addAll(interestsList);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        interestAdapter.setModified();
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.done:
                Intent intent = new Intent();

                if(OPEN_FORM == OpenForm.EDIT_PROFILE){
                    intent.putExtra("profileModel", profile);
                }
                if(OPEN_FORM == OpenForm.DISCOVER_PROFILE || OPEN_FORM == OpenForm.DISCOVER_ACTIVITY || OPEN_FORM == OpenForm.CONNECTION_ACTIVITY){
                    intent.putExtra("filterModel", filter);
                }

                setResult(RESULT_OK, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
