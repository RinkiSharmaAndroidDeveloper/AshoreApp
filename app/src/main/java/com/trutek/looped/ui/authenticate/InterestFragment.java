package com.trutek.looped.ui.authenticate;

import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CategoryModel;
import com.trutek.looped.data.contracts.models.InterestModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.ICategoryService;
import com.trutek.looped.data.contracts.services.IInterestService;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.contracts.PageQuery;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.authenticate.Adapter.InterestAdapterSignUp;
import com.trutek.looped.ui.base.BaseV4Fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Rinki on 1/19/2017.
 */

public class InterestFragment extends BaseV4Fragment {
    @Inject
    IInterestService _InterestService;

    EditText interest_search_edTx;
    RecyclerView recyclerView;
    InterestAdapterSignUp mAdapter;

    List<InterestModel> interestList;
    List<InterestModel> filterInterestList;
    List<String> selectedCategoryIds;
    ProgressDialog dialogue;
    CategoryModel categoryModel;
    ProfileModel profile;
    ProgressBar progressBar;

    public static InterestFragment newInstance(List<String> selectedCategoryIds, ProfileModel profileModel) {
        InterestFragment fragment = new InterestFragment();
        Bundle args = new Bundle();
        fragment.selectedCategoryIds = selectedCategoryIds;
        fragment.profile = profileModel;

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            //  CategoryId = extras.getString("categoryId").toString();
//            CategoryId1 = getActivity().getIntent().getStringExtra("categoryId");
        }
        View view = inflater.inflate(R.layout.fragment_signup_interest, container, false);
        interest_search_edTx = (EditText) view.findViewById(R.id.interest_search);
        recyclerView = (RecyclerView) view.findViewById(R.id.signup_interest_recycler_view);
        dialogue = new ProgressDialog(getActivity().getApplicationContext());
        dialogue.setTitle("");

        interestList = new ArrayList<>();
        filterInterestList = new ArrayList<>();
        if(null == profile.interests) {
            profile.interests = new ArrayList<>();
        }
        initAdapter();
        addTextListener();
        loadInterests();
        return view;
    }

    private void initAdapter() {
        mAdapter = new InterestAdapterSignUp(filterInterestList, interestSelectedActionListeners, interestUnSelectedActionListeners);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    OnActionListener<InterestModel> interestSelectedActionListeners = new OnActionListener<InterestModel>() {
        @Override
        public void notify(InterestModel interestModel) {
            profile.interests.add(interestModel);
        }
    };

    OnActionListener<InterestModel> interestUnSelectedActionListeners = new OnActionListener<InterestModel>() {
        @Override
        public void notify(InterestModel interestModel) {
            for (InterestModel model:profile.interests) {
                if(interestModel.getServerId().equals(model.getServerId())){
                    profile.interests.remove(model);
                    break;
                }
            }

        }
    };


    @Override
    protected void setupActivityComponent() {
        App.get(getActivity().getApplicationContext()).component().inject(this);
    }

    private void loadInterests() {
        _InterestService.getAll("?" + getInterestAction(selectedCategoryIds), new AsyncResult<Page<InterestModel>>() {
            @Override
            public void success(final Page<InterestModel> interestModelPage) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        saveInterest(interestModelPage.items);
                        dialogue.hide();
                    }
                });
            }

            @Override
            public void error(String error) {

            }
        });

    }

    String getInterestAction(List<String> stringList){
        Uri.Builder uriBuilder = new Uri.Builder();
        for (String value : stringList) {
            if (!value.equals(""))
                uriBuilder.appendQueryParameter("category", value);
        }
        return uriBuilder.build().getEncodedQuery();
    }

    private void saveInterest(List<InterestModel> interestModels) {

        for (InterestModel model : profile.interests){
            for (InterestModel interestModelCat: interestModels) {
                for (InterestModel interestModel: interestModelCat.getInterests()) {
                    if(model.getServerId().equals(interestModel.getServerId())){
                        interestModel.setSelected(true);
                        model.setSelected(true);
                    }
                }
            }
        }
        Iterator<InterestModel> interestModelIterator = profile.interests.iterator();

        while (interestModelIterator.hasNext()){
            if(!interestModelIterator.next().isSelected()){
                interestModelIterator.remove();
            }
        }

        mAdapter.addAllToAdapter(interestModels);
    }

    public void addTextListener() {

        interest_search_edTx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                /*filterCategoryModels.clear();

                for (InterestModel interestModel : categoryModels) {
                    if (interestModel.getName().toUpperCase().equals(editable.toString().toUpperCase())) {
                        filterCategoryModels.add(interestModel);
                    }
                }
                mAdapter.notifyDataSetChanged();*/
            }
        });

    }

}
