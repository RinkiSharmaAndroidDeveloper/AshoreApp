package com.trutek.looped.ui.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.models.ReportBugModel;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.data.contracts.services.IReportBugService;
import com.trutek.looped.data.impl.entities.Profile;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.ui.home.HomeActivity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends BaseV4Fragment implements View.OnClickListener {

    @Inject
    IProfileService profileService;

    @BindView(R.id.toggle_sound)
    ToggleButton toggle;
   /* @BindView(R.id.text_media)
    TextView textMedia;
    @BindView(R.id.text_reminder)
    TextView textReminder;*/
/*
    @BindView(R.id.text_alert_notification)
    TextView textNotification;
*/
    /*  @BindView(R.id.text_auto_download)
      TextView textAutoDownload;*/
  /*  @BindView(R.id.text_save_gallery)
    TextView textSaveGallery;*/
    /*  @BindView(R.id.text_medicine)
      TextView textMedicine;*/
  /*  @BindView(R.id.text_planner_event)
    TextView textPlannerEvent;*/
    @BindView(R.id.text_sound)
    TextView textSound;
    @BindView(R.id.text_report_bug)
    TextView textReportBug;
    @BindView(R.id.text_term_condition)
    TextView textTermCondition;
   /* @BindView(R.id.text_about_help)
    TextView textAbout;*/
    @Inject
    IReportBugService reportBugService;
    TextView profileName;
    MaskedImageView profileImage;
    RelativeLayout starredMessages,linearLayoutTerms,linearLayoutPrivacy;;
    ProfileModel profileModel;
    TextView contactUs;
    RelativeLayout relativeContact;
    private String imageURL;
    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    protected void setupActivityComponent() {
        App.get(getActivity()).component().inject(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((HomeActivity) getActivity()).setActionBarTitle(getString(R.string.drawer_text_settings));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_optional, container, false);
        profileName = (TextView) view.findViewById(R.id.profile_name);
        profileImage = (MaskedImageView) view.findViewById(R.id.profile_image);
        starredMessages = (RelativeLayout) view.findViewById(R.id.starred_messages_image);
        linearLayoutTerms = (RelativeLayout) view.findViewById(R.id.relative_terms);
        relativeContact = (RelativeLayout) view.findViewById(R.id.relative_contact);
        contactUs = (TextView) view.findViewById(R.id.text_contactUs);
        linearLayoutPrivacy = (RelativeLayout) view.findViewById(R.id.relative_privacy_policy);
        profileModel = new ProfileModel();
        activateButterKnife(view);
        initFields();
        starredMessages.setOnClickListener(this);
        linearLayoutTerms.setOnClickListener(this);
        linearLayoutPrivacy.setOnClickListener(this);
        relativeContact.setOnClickListener(this);
        loadProfileData();
        return view;
    }

    private void initFields() {
        if (PreferenceHelper.getPrefsHelper().getPreference(PreferenceHelper.SOUND, false)) {
            toggle.setChecked(true);
        } else {
            toggle.setChecked(false);
        }
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceHelper.getPrefsHelper().savePreference(PreferenceHelper.SOUND, true);
                } else {
                    PreferenceHelper.getPrefsHelper().savePreference(PreferenceHelper.SOUND, false);
                }
            }
        });
        setFonts();
    }

    public void loadProfileData() {
        profileModel = profileService.getMyProfile(null);
        if (profileModel.name != null) {
            profileName.setText(profileModel.name);
        }

        if (profileModel.picUrl != null && !profileModel.picUrl.isEmpty() && profileModel.picUrl.contains("http")) {
            displayMaskedImageByUrl(profileModel.picUrl, profileImage);
            imageURL = profileModel.picUrl;
        } else {
            displayMaskedImageByUrl("", profileImage);
        }
    }

    private void displayMaskedImageByUrl(String publicUrl, MaskedImageView maskedImageView) {
        ImageLoader.getInstance().displayImage(publicUrl, maskedImageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
    }

    private void setFonts() {
        Typeface typefaceBold = Typeface.createFromAsset(getActivity().getAssets(), Constants.AvenirNextBold);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), Constants.AvenirNextRegular);
       // textMedia.setTypeface(typeface);
      //  textReminder.setTypeface(typeface);
        //textNotification.setTypeface(typeface);
        profileName.setTypeface(typeface);
        contactUs.setTypeface(typeface);
        //  textMedicine.setTypeface(typeface);
        //  textPlannerEvent.setTypeface(typeface);
        textSound.setTypeface(typeface);
        textReportBug.setTypeface(typeface);
        textTermCondition.setTypeface(typeface);
        //textAbout.setTypeface(typeface);
    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this, view);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.starred_messages_image):
                Intent intent = new Intent(getActivity(), StarredMessagesActivity.class);
                startActivity(intent);
                break;
            case (R.id.relative_terms):
                Intent i = new Intent(getActivity(), TermsActivity.class);
                startActivity(i);
                break;
            case (R.id.relative_privacy_policy):
                Intent intentPrivacy = new Intent(getActivity(), PrivacyPolicyActivity.class);
                startActivity(intentPrivacy);
                break;
            case (R.id.relative_contact):
                Intent intentContact = new Intent(getActivity(), ContactUsActivity.class);
                startActivity(intentContact);
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @OnClick(R.id.text_report_bug)
    public void reportBug() {
        //openReportBugPopup();

    }

    public void openReportBugPopup() {
        try {
            final Dialog confirmationDialog = new Dialog(getActivity());
            confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            confirmationDialog.setCanceledOnTouchOutside(false);
            confirmationDialog.setContentView(R.layout.layout_report_bug_popup);
            final TextView reportbug_txtHead, reportbug_txtUserName, reportbug_txtShowUserName;
            final EditText reportbug_edtWriteReview;
            Button reportbug_btnCancel, reportbug_btnSubmit;
            Typeface lato_regular = Typeface.createFromAsset(getActivity().getAssets(), Constants.AvenirNextRegular);
            reportbug_txtHead = (TextView) confirmationDialog.findViewById(R.id.reportbug_txt_head);
            reportbug_txtUserName = (TextView) confirmationDialog.findViewById(R.id.reportbug_txt_userName);
            reportbug_txtShowUserName = (TextView) confirmationDialog.findViewById(R.id.reportbug_txt_show_userName);
            reportbug_edtWriteReview = (EditText) confirmationDialog.findViewById(R.id.reportbug_edt_write_review);
            reportbug_btnCancel = (Button) confirmationDialog.findViewById(R.id.reportbug_button_cancel);
            reportbug_btnSubmit = (Button) confirmationDialog.findViewById(R.id.reportbug_button_submit);
            reportbug_txtHead.setTypeface(lato_regular);
            reportbug_txtUserName.setTypeface(lato_regular);
            reportbug_txtShowUserName.setTypeface(lato_regular);
            reportbug_txtShowUserName.setText(PreferenceHelper.getPrefsHelper().getPreference(PreferenceHelper.FULL_NAME, ""));

            String name = PreferenceHelper.getPrefsHelper().getPreference(PreferenceHelper.FULL_NAME);
            Log.d("test_name", name);

            confirmationDialog.show();

            reportbug_btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmationDialog.dismiss();
                }
            });
            reportbug_btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reportbug_edtWriteReview.getText().toString().trim().length() == 0) {
                        Toast.makeText(getActivity().getApplicationContext(), "Write your review", Toast.LENGTH_LONG).show();
                    } else {
                        ReportBugModel reportBug = new ReportBugModel();
                        reportBug.description = reportbug_edtWriteReview.getText().toString();
                        reportBug(reportBug);
                        confirmationDialog.dismiss();
                    }

                }
            });
        } catch (Exception e) {
            System.out.println("Exception report bug :>" + e.getMessage());
        }
    }

    private void reportBug(ReportBugModel model) {

        reportBugService.reportBug(model, new AsyncResult<ReportBugModel>() {
            @Override
            public void success(ReportBugModel reportBugModel) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast("Thanks");
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
