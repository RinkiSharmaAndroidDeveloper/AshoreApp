package com.trutek.looped.ui.recipient;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ContactModel;
import com.trutek.looped.data.contracts.models.InviteModel;
import com.trutek.looped.data.contracts.models.ProviderModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.home.HomeActivity;
import com.trutek.looped.utils.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.trutek.looped.R.id.back_img;

public class ContactProvider extends BaseAppCompatActivity implements View.OnClickListener {
    private ArrayList<ContactModel> selectedContacts = new ArrayList<>();
    private ArrayList<ContactModel> contacts = new ArrayList<>();
    private ArrayList<ContactModel> filterContacts = new ArrayList<>();

    private OnActionListener<ContactModel> contactSelectedActionListeners;
    private OnActionListener<ContactModel> contactUnSelectedActionListeners;

    RecyclerView recyclerView;
    ContactProviderAdapter mAdapter;
    ImageView backImage;
    Button invitebutton;

    // Cursor to load contacts list
    Cursor phoneContact;
    ContentResolver contentResolver;
    ArrayList<ProviderModel> providers;
    private InviteModel invite;
    final static int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected int getContentResId() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_provider);
        init();
        insertDummyContactWrapper();
        listeners();
        initAdapter();
    }

    @Override
    protected void setupActivityComponent() {

        App.get(this).component().inject(this);
    }

    private void initAdapter() {
        mAdapter = new ContactProviderAdapter(contacts, filterContacts, contactSelectedActionListeners, contactUnSelectedActionListeners);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    private void init() {
        contentResolver = this.getContentResolver();
        backImage = (ImageView) findViewById(back_img);
        invitebutton = (Button) findViewById(R.id.invite_done_button);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        providers = new ArrayList<>();
        providers.addAll((ArrayList<ProviderModel>)getIntent().getSerializableExtra(Constants.LIST_PROVIDER));
        contactSelectedActionListeners = new OnActionListener<ContactModel>() {
            @Override
            public void notify(ContactModel contactModel) {
                if(!selectedContacts.contains(contactModel)) {
                    selectedContacts.add(contactModel);
                }
            }
        };

        contactUnSelectedActionListeners = new OnActionListener<ContactModel>() {
            @Override
            public void notify(ContactModel contactModel) {
                selectedContacts.remove(contactModel);
            }
        };

    }

    private void listeners() {
        backImage.setOnClickListener(this);
        invitebutton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == backImage.getId()) {
           finish();

        } else if (v.getId() == invitebutton.getId()) {
            if (selectedContacts.size() > 0) {
                Intent intent = new Intent();
                intent.putExtra("tst",selectedContacts);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Please select atleast one contact",Toast.LENGTH_SHORT).show();
            }
        }
    }


    int hasWriteContactsPermission = 0;

    private void insertDummyContactWrapper() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasWriteContactsPermission = this.checkSelfPermission(Manifest.permission.READ_CONTACTS);
        }
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        }
        insertDummyContact();
    }

    private void insertDummyContact() {
        phoneContact = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        ContactProvider.LoadContact loadContact = new ContactProvider.LoadContact();
        loadContact.execute();
        Log.v("load data", "loading data");
    }


    class LoadContact extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // Get Contact list from Phone
            if (phoneContact != null) {
                Log.e("count", "" + phoneContact.getCount());

                while (phoneContact.moveToNext()) {
                    String name = phoneContact.getString(phoneContact.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phoneContact.getString(phoneContact.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    ContactModel contact = new ContactModel();
                    contact.setName(name);
                    contact.setNumber(phoneNumber);
                    contact.setSelected(checkIfSelected(contact));

                    if(contact.isSelected){
                        selectedContacts.add(contact);
                    }
                    contacts.add(contact);
                    Log.v("contacts", "contacts method" + phoneNumber);
                }
                filterContacts.addAll(contacts);
                Log.v("fitlerco", ".filterrrradall method" + filterContacts.addAll(contacts));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    insertDummyContact();
                } else {
                    // Permission Denied
                    Toast.makeText(getApplicationContext(), "READ_CONTACTS Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    boolean checkIfSelected(ContactModel contactModel){
        for (ProviderModel providerModel:providers) {
            if (null == providerModel.getName()){
                providerModel.setName("");
            }
            if(null == providerModel.getPhone()){
                providerModel.setPhone("");
            }
            if(providerModel.getName().equals(contactModel.getName()) && providerModel.getPhone().equals(contactModel.getNumber())){
                return true;
            }
        }
        return false;
    }
}
