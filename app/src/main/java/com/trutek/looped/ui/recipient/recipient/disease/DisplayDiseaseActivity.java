package com.trutek.looped.ui.recipient.recipient.disease;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.DiseaseModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.services.IDiseaseService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.PageInput;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.recipient.recipient.adapter.DiseaseAdapter;
import com.trutek.looped.utils.listeners.EndlessScrollListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class DisplayDiseaseActivity extends BaseAppCompatActivity {

    static final String TAG = DisplayDiseaseActivity.class.getSimpleName();

    public class OpenForm {
        public static final int EDIT_RECIPIENT = 1;
    }

    @Inject
    IDiseaseService _DiseaseService;

    @BindView(R.id.toolbar) Toolbar toolbar;

    RecyclerView mRecyclerView;
    DiseaseAdapter diseaseAdapter;
    EditText editText_search;

    private HashMap<String, DiseaseModel> diseaseMap;
    private ArrayList<DiseaseModel> diseaseList;
    private ArrayList<DiseaseModel> mFilteredDiseasesList;
    private EndlessScrollListener scrollListenerDisease;
    private PageInput diseaseInput;
    private RecipientModel recipient;

    private int OPEN_FORM;

    @Override
    protected int getContentResId() {
        return R.layout.activity_display_disease;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initializeDiseases();
        initAdapter();
        initListeners();
    }

    private void init() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (getIntent().getIntExtra("OPEN_FORM", 0) == OpenForm.EDIT_RECIPIENT) {
            OPEN_FORM = OpenForm.EDIT_RECIPIENT;
            recipient = (RecipientModel) getIntent().getSerializableExtra("recipientModel");
        }

        editText_search = (EditText) findViewById(R.id.display_disease_editText_search);
        diseaseList = new ArrayList<>();
        mFilteredDiseasesList = new ArrayList<>();
        diseaseMap = new HashMap<>();

        diseaseInput = new PageInput();
        mRecyclerView = (RecyclerView) findViewById(R.id.display_disease_recyclerView_disease);
        diseaseAdapter = new DiseaseAdapter(diseaseList, mFilteredDiseasesList, mOnActionListenerSelect, mOnActionListenerDeselect);
    }

    private void initAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(diseaseAdapter);
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void initializeDiseases() {

        diseaseInput.pageNo = 1;
        if (scrollListenerDisease != null) {
            scrollListenerDisease.reset();
        }

        diseaseList.clear();
        diseaseMap.clear();

        if(OPEN_FORM == OpenForm.EDIT_RECIPIENT){
            List<DiseaseModel> selectedInterests = recipient.diseases;
            for(DiseaseModel item : selectedInterests){
                item.isSelected = true;
                diseaseMap.put(item.getName(), item);
            }
        }
        loadDiseases();
    }


    private void loadDiseases() {
        _DiseaseService.getAllDisease(diseaseFromServer);
    }

    AsyncResult<List<DiseaseModel>> diseaseFromServer = new AsyncResult<List<DiseaseModel>>() {
        @Override
        public void success(List<DiseaseModel> diseaseModels) {
            Log.d(TAG, "DiseaseCount: " + diseaseModels.size());

            for (DiseaseModel item : diseaseModels) {
                if (!diseaseMap.containsKey(item.getName())){
                    diseaseMap.put(item.getName(), item);
                }
            }

            diseaseList.addAll(diseaseMap.values());

            Collections.sort(diseaseList, new Comparator<DiseaseModel>() {
                @Override
                public int compare(DiseaseModel lhs, DiseaseModel rhs) {
                    if (lhs.isSelected && !rhs.isSelected)
                        return -1;

                    if (lhs.isSelected && rhs.isSelected)
                        return 0;

                    if (!lhs.isSelected && rhs.isSelected)
                        return 1;

                    return 0;
                }
            });

            mFilteredDiseasesList.addAll(diseaseList);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    diseaseAdapter.setModified();
                }
            });
        }

        @Override
        public void error(String error) {

        }
    };

    OnActionListener<DiseaseModel> mOnActionListenerSelect = new OnActionListener<DiseaseModel>() {
        @Override
        public void notify(DiseaseModel diseaseModel) {
            if(OPEN_FORM == OpenForm.EDIT_RECIPIENT) {
                recipient.diseases.add(diseaseModel);
            }
        }
    };

    OnActionListener<DiseaseModel> mOnActionListenerDeselect = new OnActionListener<DiseaseModel>() {
        @Override
        public void notify(DiseaseModel diseaseModel) {
            if(OPEN_FORM == OpenForm.EDIT_RECIPIENT) {
                recipient.diseases.remove(diseaseModel);
            }
        }
    };

    private void initListeners() {
        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                diseaseAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                break;

            case R.id.done :
                Intent intent = new Intent();
                intent.putExtra(Constants.INTENT_KEY_DISEASE, diseaseList);
                intent.putExtra("Key", "Value");
                intent.putExtra("recipientModel", recipient);
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
