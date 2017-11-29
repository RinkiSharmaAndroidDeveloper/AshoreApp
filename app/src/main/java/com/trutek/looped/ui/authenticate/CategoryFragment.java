package com.trutek.looped.ui.authenticate;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CategoryModel;
import com.trutek.looped.data.contracts.services.ICategoryService;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.authenticate.Adapter.CategoryAdapter;
import com.trutek.looped.ui.base.BaseV4Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Rinki on 1/19/2017.
 */
public class CategoryFragment  extends BaseV4Fragment {

    @Inject
    ICategoryService _categoryService;

    List<CategoryModel> categoryModels;

    RecyclerView recyclerView;
    CategoryAdapter mAdapter;

    PageInput pageInput;

    CategoryFragmentListener mListener;

    public static CategoryFragment newInstance(List<CategoryModel> categoryModels) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        fragment.categoryModels = categoryModels;

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_category, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.signup_category_recycler_view);
        if(null == categoryModels) {
            categoryModels = new ArrayList<>();
        }
        pageInput = new PageInput();
        initAdapter();
        loadCategory();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof CategoryFragmentListener){
            mListener = (CategoryFragmentListener) context;
        }else{
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void initAdapter() {
        mAdapter = new CategoryAdapter(categoryModels,asyncResult_selectedCategory);
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }
    private void loadCategory()
    {
        _categoryService.getAllCategory(pageInput,new AsyncResult<Page<CategoryModel>>() {

            @Override
            public void success(final Page<CategoryModel> categoryModelPage) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        saveCategory(categoryModelPage.items);
                    }
                });
            }

            @Override
            public void error(String error) {

            }
        });
    }

    private void saveCategory(List<CategoryModel> categoryModel) {

        for (CategoryModel model: categoryModel) {
            for (CategoryModel catModel: categoryModels) {
                if(catModel.getServerId().equals(model.getServerId())){
                    model.setSelected(true);
                    mListener.selectedCategory(model);
                    break;
                }
            }
        }
        mAdapter.addAllToAdapter(categoryModel);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(getActivity().getApplicationContext()).component().inject(this);
    }

    public interface CategoryFragmentListener{
        void selectedCategory(CategoryModel categoryModel);
        void removeSelectedCategory(String categoryId);
    }

    AsyncResult<CategoryModel> asyncResult_selectedCategory = new AsyncResult<CategoryModel>() {
        @Override
        public void success(CategoryModel categoryModel) {
            mListener.selectedCategory(categoryModel);
        }

        @Override
        public void error(String error) {
            mListener.removeSelectedCategory(error);
        }
    };
}
