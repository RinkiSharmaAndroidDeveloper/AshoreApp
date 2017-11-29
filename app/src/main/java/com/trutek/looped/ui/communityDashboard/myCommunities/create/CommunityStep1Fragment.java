package com.trutek.looped.ui.communityDashboard.myCommunities.create;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.TagModel;
import com.trutek.looped.data.contracts.services.IInterestService;
import com.trutek.looped.data.contracts.services.ITagService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.ui.profile.create.adapter.InterestAdapter;
import com.trutek.looped.ui.profile.create.adapter.TagAdapter;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.listeners.EndlessScrollListener;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommunityStep1Fragment extends BaseV4Fragment {

    public final static String COMMUNITY_STEP_TWO = "communityStep2Fragment";
    final static String ERROR_NO_TAG = "Please select atleast one topic";
    final static String ERROR_NO_INTEREST = "Please select atleast one interest";

    @Inject
    ITagService tagService;
    @Inject
    IInterestService interestService;

    @BindView(R.id.edit_new_community_step_one_community_type)EditText edit_community_type_search;
    @BindView(R.id.edit_new_community_step_one_community_about)EditText edit_community_about;
    @BindView(R.id.recycler_step_one_new_community_type)RecyclerView recyclerViewCommunityType;
    @BindView(R.id.recycler_step_one_new_community_about)RecyclerView recyclerViewCommunityAbout;
    @BindView(R.id.create_community_button_next) Button button_next;

    private EndlessScrollListener scrollListenerTopic;
    private EndlessScrollListener scrollListenerInterest;

    private OnActionListener<TagModel> tagSelectedActionListeners;
    private OnActionListener<TagModel> tagUnSelectedActionListeners;
    private OnActionListener<InterestModel> interestSelectedActionListeners;
    private OnActionListener<InterestModel> interestUnSelectedActionListeners;

    private TagAdapter tagAdapter;
    private InterestAdapter interestAdapter;

    private ArrayList<TagModel> tagList;
    private ArrayList<InterestModel> interestsList;

    private ArrayList<InterestModel> filteredInterests;
    private ArrayList<TagModel> filteredTags;

    PageInput tagInput;
    PageInput interestsInput;

    private CommunityModel community;
    private OnFragmentInteractionListener mListener;
    String errorMessage;

    public CommunityStep1Fragment() {
        // Required empty public constructor
    }

    public static CommunityStep1Fragment newInstance(CommunityModel model) {
        CommunityStep1Fragment fragment = new CommunityStep1Fragment();
        Bundle args = new Bundle();
        args.putSerializable(CreateCommunityActivity.COMMUNITY_MODEL, model);
        fragment.setArguments(args);
        return fragment;
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

        tagAdapter = new TagAdapter(tagList, filteredTags, tagSelectedActionListeners, tagUnSelectedActionListeners);
        interestAdapter = new InterestAdapter(interestsList, filteredInterests, interestSelectedActionListeners, interestUnSelectedActionListeners);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(getActivity()).component().inject(this);
    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this,view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_step1, container, false);
        activateButterKnife(view);

        initializeTags();
        initializeInterests();

        initAdapters();
        initListeners();

        setFonts();
        return  view;
    }

    private void setFonts() {
        Typeface avenirNextRegular=Typeface.createFromAsset(getActivity().getAssets(), Constants.AvenirNextRegular);
        edit_community_about.setTypeface(avenirNextRegular);
        edit_community_type_search.setTypeface(avenirNextRegular);
        button_next.setTypeface(avenirNextRegular);
    }

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

    @OnClick(R.id.create_community_button_next)
    public void nextClick(){
        if(isRequiredFieldSelected()) {
            ((CreateCommunityActivity) getActivity()).addFragmentWithStackEntry(R.id.create_community_frame, CommunityStep2Fragment.newInstance(community), COMMUNITY_STEP_TWO);
        }else {
            ToastUtils.shortToast(errorMessage);
        }
    }


    private void init() {
        community = (CommunityModel) getArguments().getSerializable(CreateCommunityActivity.COMMUNITY_MODEL);
        community.interests = new ArrayList<>();
        community.tags = new ArrayList<>();

        tagSelectedActionListeners = new OnActionListener<TagModel>() {
            @Override
            public void notify(TagModel tagModel) {
                community.tags.add(tagModel);
            }
        };

        tagUnSelectedActionListeners = new OnActionListener<TagModel>() {
            @Override
            public void notify(TagModel tagModel) {
                community.tags.remove(tagModel);
            }
        };

        interestSelectedActionListeners  = new OnActionListener<InterestModel>() {
            @Override
            public void notify(InterestModel interestModel) {
                community.interests.add(interestModel);
            }
        };

        interestUnSelectedActionListeners  = new OnActionListener<InterestModel>() {
            @Override
            public void notify(InterestModel interestModel) {
                community.interests.remove(interestModel);
            }
        };
    }

    private void initListeners() {
        edit_community_type_search.addTextChangedListener(new TextWatcher() {
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

        edit_community_about.addTextChangedListener(new TextWatcher() {
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

        final LinearLayoutManager layoutManagerTopic = new LinearLayoutManager(getActivity());
        final LinearLayoutManager layoutManagerInterest = new LinearLayoutManager(getActivity());

        recyclerViewCommunityType.setLayoutManager(layoutManagerTopic);
        recyclerViewCommunityAbout.setLayoutManager(layoutManagerInterest);

        recyclerViewCommunityType.setAdapter(tagAdapter);
        recyclerViewCommunityAbout.setAdapter(interestAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    private void initializeTags() {

        tagInput.pageNo = 1;
        if (scrollListenerTopic != null) {
            scrollListenerTopic.reset();
        }

        tagList.clear();
//        ArrayList<InterestItem> selectedInterests = NayberContext.getCurrent().getProfile().interests;
//        ArrayList<InterestItem> selectedInterests = mSelectedInterests;
//        for(InterestItem item : selectedInterests){
//            item.isSelected = true;
//            interestsList.add(InterestModel.fromItem(item));
//        }
        loadTopics();
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

    private void initializeInterests() {

        interestsInput.pageNo = 1;
        if (scrollListenerInterest != null) {
            scrollListenerInterest.reset();
        }

        interestsList.clear();
//        ArrayList<InterestItem> selectedInterests = NayberContext.getCurrent().getProfile().interests;
//        ArrayList<InterestItem> selectedInterests = mSelectedInterests;
//        for(InterestItem item : selectedInterests){
//            item.isSelected = true;
//            interestsList.add(InterestModel.fromItem(item));
//        }
        loadInterests();
    }

    private void loadInterests() {
        interestService.getAll(interestsInput, new AsyncResult<Page<InterestModel>>() {
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

    boolean isRequiredFieldSelected(){
        if(null != community.tags && community.tags.size()==0){
            errorMessage = ERROR_NO_TAG;
            return false;
        }else if(null != community.interests && community.interests.size() == 0){
            errorMessage = ERROR_NO_INTEREST;
            return false;
        }else {
            return true;
        }
    }
}
