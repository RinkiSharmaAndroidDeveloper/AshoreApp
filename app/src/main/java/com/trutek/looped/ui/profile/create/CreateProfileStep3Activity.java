package com.trutek.looped.ui.profile.create;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.data.contracts.models.ContactModel;
import com.trutek.looped.data.contracts.models.InviteModel;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.home.HomeActivity;
import com.trutek.looped.R;
import com.trutek.looped.ui.profile.create.adapter.InviteAdapterRecycler;
import com.trutek.looped.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;


public class CreateProfileStep3Activity extends BaseAppCompatActivity {

    private static final int INVITATION_SENT = 10;

    @Inject
    IProfileService profileService;

    @BindView(R.id.recycler_invite_contacts)
    RecyclerView recyclerView_invite_people;
    @BindView(R.id.invite_done_button)
    Button button_done;
    @BindView(R.id.edit_text_contact_search)
    EditText search;

    private ArrayList<ContactModel> selectedContacts;
    private ArrayList<ContactModel> contacts;
    private ArrayList<ContactModel> filterContacts;
    private InviteModel invite;


    // Cursor to load contacts list
    Cursor phoneContact;
    final static int REQUEST_CODE_ASK_PERMISSIONS = 123;

    ContentResolver contentResolver;
    InviteAdapterRecycler inviteAdapterRecycler;

    private OnActionListener<ContactModel> contactSelectedActionListeners;
    private OnActionListener<ContactModel> contactUnSelectedActionListeners;

    @Override
    protected int getContentResId() {
        return R.layout.activity_create_profile_activity_step3_invite;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();

        setFonts();
        insertDummyContactWrapper();
        initAdapter();
        initListeners();
    }

    public void initFields() {
        contacts = new ArrayList<>();
        filterContacts = new ArrayList<>();
        contentResolver = this.getContentResolver();
        selectedContacts = new ArrayList<>();
        contactSelectedActionListeners = new OnActionListener<ContactModel>() {
            @Override
            public void notify(ContactModel contactModel) {
                selectedContacts.add(contactModel);
            }
        };

        contactUnSelectedActionListeners = new OnActionListener<ContactModel>() {
            @Override
            public void notify(ContactModel contactModel) {
                selectedContacts.remove(contactModel);
            }
        };
    }

    private void initAdapter() {
        inviteAdapterRecycler = new InviteAdapterRecycler(contacts, filterContacts, contactSelectedActionListeners, contactUnSelectedActionListeners);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView_invite_people.setLayoutManager(linearLayoutManager);
        recyclerView_invite_people.setAdapter(inviteAdapterRecycler);
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

    private void insertDummyContact() {
        phoneContact = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        LoadContact loadContact = new LoadContact();
        loadContact.execute();
    }

    private void initListeners() {
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inviteAdapterRecycler.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void setFonts() {
        Typeface avenirNextRegular = Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        button_done.setTypeface(avenirNextRegular);
    }

    @OnClick(R.id.invite_done_button)
    public void done() {
        invite = new InviteModel();
        invite.contacts = selectedContacts;

        if (selectedContacts.size() > 0) {
            sendInvitation(invite);
        } else {
            HomeActivity.start(getApplicationContext());
            setProfileStatusDone();
        }
    }

    public class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone
            if (phoneContact != null) {
                Log.e("count", "" + phoneContact.getCount());

                while (phoneContact.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = phoneContact.getString(phoneContact.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phoneContact.getString(phoneContact.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phoneContact.getString(phoneContact.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String EmailAddr = phoneContact.getString(phoneContact.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));
                    String image_thumb = phoneContact.getString(phoneContact.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                    try {
                        if (image_thumb != null) {
                            bit_thumb = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(image_thumb));
                        } else {
                            Log.e("No Image Thumb", "");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ContactModel contact = new ContactModel();
                    contact.setThumb(bit_thumb);
                    contact.setName(name);
                    contact.setNumber(phoneNumber);
                    contact.setEmail(id);
                    contact.setSelected(false);
                    contacts.add(contact);
                }
                filterContacts.addAll(contacts);

            } else {
                Log.e("Cursor close 1", "");
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            inviteAdapterRecycler.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (phoneContact != null) {
            phoneContact.close();
        }
    }

    private void sendInvitation(final InviteModel invite) {

        int count = contactHaveNumber(invite);

        if (count == 0) {
            ToastUtils.longToast(getString(R.string.dp_noPhoneNumberFound));
        } else if (invite.contacts.size() == count) {
            sendTextMessage(invite);
        } else {
            Toast.makeText(getApplicationContext(), "Some contact doesn't have numbers", Toast.LENGTH_SHORT).show();
            sendTextMessage(invite);
        }
    }

    private void notifyServerAboutInvitation() {
        profileService.sendInvitation(invite, new AsyncResult<InviteModel>() {
            @Override
            public void success(InviteModel inviteModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HomeActivity.start(getApplicationContext());
                        setProfileStatusDone();
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

    private void sendTextMessage(InviteModel invite) {
        String numbers = "";
        for (ContactModel contact : invite.contacts) {
            numbers = numbers + ";" + contact.number;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String message = getString(R.string.cdi_sms_invitaton_text);
        Uri data = Uri.parse("sms:" + numbers);
        intent.setData(data);
        intent.putExtra("sms_body", message);
        intent.putExtra("exit_on_sent", true);
        startActivityForResult(intent, INVITATION_SENT);
    }

    private int contactHaveNumber(InviteModel invite) {
        int count = 0;

        for (ContactModel contact : invite.contacts) {
            if (!contact.number.isEmpty() && contact.number != null) {
                count++;
            }
        }
        return count;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INVITATION_SENT && resultCode == RESULT_OK) {
            ToastUtils.longToast(getString(R.string.cdi_invitaion_sent_message));
            notifyServerAboutInvitation();
        }
    }
}