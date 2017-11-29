package com.trutek.looped.ui.profile.create;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.contracts.services.IInterestService;
import com.trutek.looped.data.contracts.services.ITagService;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.ui.profile.create.adapter.TagAdapter;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.listeners.EndlessScrollListener;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.ui.profile.create.adapter.InterestAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateProfileStep1InterestFragment extends BaseV4Fragment {

    @Inject
    ITagService tagService;
    @Inject
    IInterestService _InterestService;

    @BindView(R.id.edit_text_topic_search) EditText tag_search;
    @BindView(R.id.edit_text_interest_search) EditText interest_search;
    @BindView(R.id.recycler_view_topic) RecyclerView recyclerViewTopic;
    @BindView(R.id.recycler_view_interest) RecyclerView recyclerViewInterest;

    private ProfileModel profile;
    private EndlessScrollListener scrollListenerTag;
    private EndlessScrollListener scrollListenerInterest;

    private OnFragmentInteractionListener mListener;
    private OnActionListener<TagModel> tagSelectedActionListeners;
    private OnActionListener<TagModel> tagUnSelectedActionListeners;
    private OnActionListener<InterestModel> interestSelectedActionListeners;
    private OnActionListener<InterestModel> interestUnSelectedActionListeners;

    private TagAdapter tagAdapter;
    private InterestAdapter interestAdapter;

    private ArrayList<TagModel> tagList;
    private ArrayList<InterestModel> interestsList;

    private ArrayList<TagModel> filteredTags;
    private ArrayList<InterestModel> filteredInterests;

    PageInput tagInput;
    PageInput interestsInput;

    public CreateProfileStep1InterestFragment() {
        // Required empty public constructor
    }

    public static CreateProfileStep1InterestFragment newInstance(ProfileModel profile) {
        CreateProfileStep1InterestFragment fragment = new CreateProfileStep1InterestFragment();
        Bundle args = new Bundle();
        args.putSerializable("profile", profile);
        fragment.setArguments(args);
        return fragment;
    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this,view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        tagInput = new PageInput();
        interestsInput = new PageInput();

        tagList = new ArrayList<>();
        interestsList = new ArrayList<>();

        filteredTags = new ArrayList<>();
        filteredInterests = new ArrayList<>();

        profile.interests = new ArrayList<>();
        profile.tags = new ArrayList<>();

        tagAdapter = new TagAdapter(tagList, filteredTags, tagSelectedActionListeners, tagUnSelectedActionListeners);
        interestAdapter = new InterestAdapter(interestsList, filteredInterests, interestSelectedActionListeners, interestUnSelectedActionListeners);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(getActivity()).component().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_interest, container, false);
        activateButterKnife(view);

        initializeTags();
        initializeInterests();

        initAdapters();
        initListeners();
        return view;
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

    private void initializeTags() {

        tagInput.pageNo = 1;
        if (scrollListenerTag != null) {
            scrollListenerTag.reset();
        }

        tagList.clear();
        loadTopics();
    }

    private void initializeInterests() {

        interestsInput.pageNo = 1;
        if (scrollListenerInterest != null) {
            scrollListenerInterest.reset();
        }

        interestsList.clear();
        loadInterests();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

//    private void initListeners(){
//
//        LinearLayoutManager layoutManagerTopic = (LinearLayoutManager) recyclerViewTopic.getLayoutManager();
//        scrollListenerTag = new EndlessScrollListener(layoutManagerTopic) {
//            @Override
//            public void onLoadMore(int currentPage) {
//                topicInput.pageNo = currentPage;
//                loadTopics();
//            }
//        };
//
//        LinearLayoutManager layoutManagerInterest = (LinearLayoutManager) recyclerViewInterest.getLayoutManager();
//        scrollListenerInterest = new EndlessScrollListener(layoutManagerInterest) {
//            @Override
//            public void onLoadMore(int currentPage) {
//                interestsInput.pageNo = currentPage;
//                loadInterests();
//            }
//        };
//    }

    private void initAdapters() {
//        recyclerViewTopic.addOnScrollListener(scrollListenerTag);
//        recyclerViewInterest.addOnScrollListener(scrollListenerInterest);

        final LinearLayoutManager layoutManagerTopic = new LinearLayoutManager(getActivity());
        final LinearLayoutManager layoutManagerInterest = new LinearLayoutManager(getActivity());

        recyclerViewTopic.setLayoutManager(layoutManagerTopic);
        recyclerViewInterest.setLayoutManager(layoutManagerInterest);

        recyclerViewTopic.setAdapter(tagAdapter);
        recyclerViewInterest.setAdapter(interestAdapter);
    }

    private void init(){
        profile = (ProfileModel) getArguments().getSerializable("profile");

        tagSelectedActionListeners = new OnActionListener<TagModel>() {
            @Override
            public void notify(TagModel tagModel) {
                profile.tags.add(tagModel);
            }
        };

        tagUnSelectedActionListeners = new OnActionListener<TagModel>() {
            @Override
            public void notify(TagModel tagModel) {
                profile.tags.remove(tagModel);
            }
        };

        interestSelectedActionListeners  = new OnActionListener<InterestModel>() {
            @Override
            public void notify(InterestModel interestModel) {
                interestModel.setProfileId(profile.getId());
                profile.interests.add(interestModel);
            }
        };

        interestUnSelectedActionListeners  = new OnActionListener<InterestModel>() {
            @Override
            public void notify(InterestModel interestModel) {
                profile.interests.remove(interestModel);
            }
        };
    }

    private void loadTopics() {
        tagService.getAll(tagInput, new AsyncResult<Page<TagModel>>() {
            @Override
            public void success(Page<TagModel> models) {

                for (TagModel item : models.items) {
                    if(!tagList.contains(item)){
                        tagList.add(item);
                    }
                }
                filteredTags.addAll(tagList);

//
//                Collections.sort(tagList, new Comparator<InterestModel>() {
//                    @Override
//                    public int compare(InterestModel lhs, InterestModel rhs) {
//                        if (lhs.isSelected && !rhs.isSelected)
//                            return -1;
//
//                        if (lhs.isSelected && rhs.isSelected)
//                            return 0;
//
//                        if (!lhs.isSelected && rhs.isSelected)
//                            return 1;
//
//                        return 0;
//                    }
//                });

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tagAdapter.setModified();
                    }
                });
            }

            @Override
            public void error(final String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(error);
                    }
                });
            }
        });

    }

    private void loadInterests() {
        _InterestService.getAll(interestsInput, new AsyncResult<Page<InterestModel>>() {
            @Override
            public void success(Page<InterestModel> models) {

                for (InterestModel item : models.items) {
                    if(!interestsList.contains(item)){
                        interestsList.add(item);
                    }
                }
                filteredInterests.addAll(interestsList);

//                Collections.sort(interestsList, new Comparator<InterestModel>() {
//                    @Override
//                    public int compare(InterestModel lhs, InterestModel rhs) {
//                        if (lhs.isSelected && !rhs.isSelected)
//                            return -1;
//
//                        if (lhs.isSelected && rhs.isSelected)
//                            return 0;
//
//                        if (!lhs.isSelected && rhs.isSelected)
//                            return 1;
//
//                        return 0;
//                    }
//                });

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        interestAdapter.setModified();
                    }
                });
            }

            @Override
            public void error(final String error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(error);
                    }
                });
            }
        });
    }

}
