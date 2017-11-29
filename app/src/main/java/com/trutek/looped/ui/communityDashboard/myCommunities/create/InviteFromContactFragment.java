package com.trutek.looped.ui.communityDashboard.myCommunities.create;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ContactModel;
import com.trutek.looped.data.contracts.models.InviteModel;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.contracts.OnActionListener;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.ui.communityDashboard.myCommunities.MyCommunitiesActivity;
import com.trutek.looped.ui.profile.create.adapter.InviteAdapterRecycler;
import com.trutek.looped.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteFromContactFragment extends BaseV4Fragment {

    private static final int INVITATION_SENT = 10;

    @Inject
    IProfileService profileService;

    @BindView(R.id.recycler_invite_contacts) RecyclerView recyclerView;
    @BindView(R.id.edit_text_contact_search) EditText search;
    @BindView(R.id.create_community_button_next)Button button_next;

    private ArrayList<ContactModel> contacts;
    private ArrayList<ContactModel> selectedContacts;
    private ArrayList<ContactModel> filterContacts;
    private Cursor phoneContact;

    private InviteModel invite;

    private ContentResolver contentResolver;
    private InviteAdapterRecycler inviteAdapterRecycler;

    private OnActionListener<ContactModel> contactSelectedActionListeners;
    private OnActionListener<ContactModel> contactUnSelectedActionListeners;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private OnFragmentInteractionListener mListener;

    public InviteFromContactFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static InviteFromContactFragment newInstance() {
        InviteFromContactFragment fragment = new InviteFromContactFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
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
        View view= inflater.inflate(R.layout.fragment_from_contact, container, false);
        activateButterKnife(view);

        insertDummyContactWrapper();
        initAdapter();
        initListeners();
        setFonts();
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private void setFonts() {
        Typeface avenirNextRegular=Typeface.createFromAsset(getActivity().getAssets(), Constants.AvenirNextRegular);
        button_next.setTypeface(avenirNextRegular);
    }

    public void initFields(){
        contacts = new ArrayList<>();
        filterContacts = new ArrayList<>();
        contentResolver = getActivity().getContentResolver();
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
        inviteAdapterRecycler = new InviteAdapterRecycler(contacts, filterContacts, contactSelectedActionListeners,contactUnSelectedActionListeners);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(inviteAdapterRecycler);
    }

    private void insertDummyContactWrapper() {
        int hasWriteContactsPermission = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasWriteContactsPermission = getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS);
        }
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
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
                    Toast.makeText(getActivity(), "READ_CONTACTS Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void insertDummyContact() {
        phoneContact = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
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

    class LoadContact extends AsyncTask<Void, Void, Void> {
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void sendInvitation(final InviteModel invite) {

        int count = contactHaveNumber(invite);

        if (count == 0) {
            ToastUtils.longToast(getString(R.string.dp_noPhoneNumberFound));
        } else if (invite.contacts.size() == count) {
            sendTextMessage(invite);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Some contact doesn't have numbers", Toast.LENGTH_SHORT).show();
            sendTextMessage(invite);
        }
    }

    private void notifyServerAboutInvitation(){
        profileService.sendInvitation(invite, new AsyncResult<InviteModel>() {
            @Override
            public void success(InviteModel inviteModel) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(getString(R.string.cdi_invitaion_sent_message));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == INVITATION_SENT && resultCode == CreateCommunityActivity.RESULT_OK) {
            notifyServerAboutInvitation();
        }
    }

    @OnClick(R.id.create_community_button_next)
    public void nextClick(){
        invite = new InviteModel();
        invite.contacts = selectedContacts;

        if (invite.contacts.size() > 0) {
            sendInvitation(invite);
        } else {
            Intent intent =new Intent(getContext().getApplicationContext(), MyCommunitiesActivity.class);
            startActivity(intent);
        }
    }
}
