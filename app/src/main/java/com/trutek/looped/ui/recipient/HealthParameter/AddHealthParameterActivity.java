package com.trutek.looped.ui.recipient.healthparameter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.HealthParameterModel;
import com.trutek.looped.data.contracts.services.IHealthParamService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.ui.base.BaseAppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public class AddHealthParameterActivity extends BaseAppCompatActivity implements View.OnClickListener{

    @Inject
    IHealthParamService _HealthParamService;

    TextView textView_title;
    ImageView imageView_back;
    RecyclerView mRecyclerView;

    AddHealthParameterAdapter addHealthParameterAdapter;

    List<HealthParameterModel> mHealthParameterModels;
    List<HealthParameterModel> mFilteredHealthParameterModels;

    private HashMap<String, HealthParameterModel> mapHealthParam;

    @Override
    protected int getContentResId() {
        return R.layout.activity_add_health_parameter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setFonts();
        listeners();
        initHealthParam();
        initAdapter();
        getAllHealthParams();
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void initViews() {
        textView_title = (TextView) findViewById(R.id.add_health_parameter_textView_title);
        imageView_back = (ImageView) findViewById(R.id.add_health_parameter_imageView_back);
        mRecyclerView = (RecyclerView) findViewById(R.id.add_health_parameter_recyclerView);
        mHealthParameterModels = new ArrayList<>();
        mFilteredHealthParameterModels = new ArrayList<>();
        mapHealthParam = new HashMap<>();
        addHealthParameterAdapter = new AddHealthParameterAdapter(mHealthParameterModels,mFilteredHealthParameterModels,asyncResult_ListenerAddHealth);
    }

    private void setFonts() {
        textView_title.setTypeface(avenirNextRegular);
    }

    private void listeners() {
        imageView_back.setOnClickListener(this);
    }

    private void initHealthParam() {
        ArrayList<HealthParameterModel> healthParameterModels = (ArrayList<HealthParameterModel>)getIntent().getSerializableExtra("lists");
        for (HealthParameterModel healthParameterModel:healthParameterModels) {
            mapHealthParam.put(healthParameterModel.getName(),healthParameterModel);
        }
    }

    private void initAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(addHealthParameterAdapter);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == imageView_back.getId()){
            finish();
        }
    }

    AsyncResult<HealthParameterModel> asyncResult_ListenerAddHealth = new AsyncResult<HealthParameterModel>() {
        @Override
        public void success(HealthParameterModel healthParameterModel) {
            Intent intent = new Intent();
            intent.putExtra(Constants.MODEL_HEALTHPARAM,healthParameterModel);
            setResult(RESULT_OK,intent);
            finish();
        }

        @Override
        public void error(String error) {
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        }
    };

    void getAllHealthParams(){
        _HealthParamService.getAll(new AsyncResult<Page<HealthParameterModel>>() {
            @Override
            public void success(Page<HealthParameterModel> healthParameterModelPage) {

                for (HealthParameterModel item : healthParameterModelPage.items) {
                    if(!mapHealthParam.containsKey(item.getName())){
                        mapHealthParam.put(item.getName(), item);
                    }
                }

                mHealthParameterModels.addAll(mapHealthParam.values());

               /* Collections.sort(interestsList, new Comparator<InterestModel>() {
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
                });*/

                mHealthParameterModels.addAll(mFilteredHealthParameterModels);

                mFilteredHealthParameterModels.addAll(mHealthParameterModels);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addHealthParameterAdapter.setModified();
                    }
                });
            }

            @Override
            public void error(String error) {

            }
        });
    }

}
