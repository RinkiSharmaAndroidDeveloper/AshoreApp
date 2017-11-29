package com.trutek.looped.ui.recipient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ContactModel;
import com.trutek.looped.data.contracts.models.ProviderModel;
import com.trutek.looped.data.contracts.models.RecipientModel;
import com.trutek.looped.data.contracts.services.IProviderService;
import com.trutek.looped.data.contracts.services.IRecipientService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.ui.base.BaseAppCompatActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.trutek.looped.R.id.back_img;

public class RecipientProviderActivity extends BaseAppCompatActivity implements View.OnClickListener {

    final static String TAG = RecipientProviderActivity.class.getSimpleName();
    @Inject
    IRecipientService _RecipientService;

    @Inject
    IProviderService _providerService;

    final static int REQUEST_CONTACTS = 1;
    private ArrayList<ProviderModel> providers = new ArrayList<>();
    private OnActionListener<ProviderModel> contactSelectedActionListeners;
    private OnActionListener<ProviderModel> contactUnSelectedActionListeners;
    ImageView backImage, addContact;
    RecipientProviderAdapter adapter;
    RecyclerView recyclerView;
    RecipientModel mRecipientModel;


    @Override
    protected int getContentResId() {
        return R.layout.activity_recipient_provider_board;
    }

    BroadcastReceiver mProviderBroadCast =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        listeners();
        initAdapter();
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void init() {

        backImage = (ImageView) findViewById(back_img);
        addContact = (ImageView) findViewById(R.id.add_contact);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecipientModel = (RecipientModel) getIntent().getSerializableExtra(Constants.MODEL_RECIPIENT);
        providers.addAll(_providerService.getProvidersLocally(mRecipientModel.getId()));

    }

    private void listeners() {
        backImage.setOnClickListener(this);
        addContact.setOnClickListener(this);
    }

    private void initAdapter() {
        adapter = new RecipientProviderAdapter(providers, contactSelectedActionListeners, contactUnSelectedActionListeners);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == backImage.getId()) {
            finish();
        } else if (v.getId() == addContact.getId()) {
            Intent intent = new Intent(RecipientProviderActivity.this, ContactProvider.class);
            intent.putExtra(Constants.LIST_PROVIDER,providers);
            startActivityForResult(intent, REQUEST_CONTACTS);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONTACTS && resultCode == RESULT_OK && null != data) {
            providers.clear();
            _providerService.deletePreviousProviders();
            ArrayList<ContactModel> contactModels = (ArrayList<ContactModel>) data.getSerializableExtra("tst");

            Log.d(TAG,"RecipientID: "+String.valueOf(mRecipientModel.getId()));

            for (ContactModel contactModel: contactModels) {
                ProviderModel providerModel =new ProviderModel();
                providerModel.setName(contactModel.getName());
                providerModel.setPhone(contactModel.getNumber());
                providerModel.setRecipientId(mRecipientModel.getId());
                providers.add(providerModel);
            }
            mRecipientModel.setProviders(providers);
            showProgress();
            _RecipientService.updateRecipient(mRecipientModel,asyncResult_updateRecipient);
        }

    }

    AsyncResult<RecipientModel> asyncResult_updateRecipient = new AsyncResult<RecipientModel>() {
        @Override
        public void success(RecipientModel recipientModel) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideProgress();
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void error(String error) {

        }
    };
}
