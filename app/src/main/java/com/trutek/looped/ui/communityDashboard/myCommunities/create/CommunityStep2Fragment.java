package com.trutek.looped.ui.communityDashboard.myCommunities.create;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.CommunityModel;
import com.trutek.looped.data.contracts.models.LocationModel;
import com.trutek.looped.data.contracts.services.ICommunityService;
import com.trutek.looped.geoCode.GeoCodeIntentService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.activity.create.AddressUtil;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.helpers.ImagePickHelper;
import com.trutek.looped.utils.image.ImageUtils;
import com.trutek.looped.utils.listeners.OnImagePickedListener;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommunityStep2Fragment extends BaseV4Fragment implements View.OnClickListener {

    MaskedImageView communityPic;
    @BindView(R.id.new_community_name)EditText edit_community_name;
    @BindView(R.id.new_community_add_description)EditText edit_community_description;
    @BindView(R.id.new_community_location_editText)TextView community_location;
    @BindView(R.id.create_community_button_next) Button button_next;
    @BindView(R.id.scrollView) ScrollView scrollView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;

    private AddressResultReceiver mResultReceiver;

    private CommunityModel community;
    private OnFragmentInteractionListener mListener;
    private LocationModel location;
    private ImagePickHelper imagePickHelper;
    String address;
    String name;
    TextView label,privacyText,groupPostText,textView_imageTitle;
    ToggleButton togle_privacy,toggle_group;
    EditText enterLocation;
    public CommunityStep2Fragment() {
        // Required empty public constructor
    }

    public static CommunityStep2Fragment newInstance(CommunityModel model) {
        CommunityStep2Fragment fragment = new CommunityStep2Fragment();
        Bundle args = new Bundle();
        args.putSerializable(CreateCommunityActivity.COMMUNITY_MODEL, model);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickHelper = new ImagePickHelper();
        mResultReceiver = new AddressResultReceiver(null);
        community = (CommunityModel) getArguments().getSerializable(CreateCommunityActivity.COMMUNITY_MODEL);
        community.location =new LocationModel();
    }

    @Override
    protected void setupActivityComponent() {

    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this,view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community_step2, container, false);
        privacyText =(TextView) view.findViewById(R.id.community_textview_privacy);
        toggle_group =(ToggleButton) view.findViewById(R.id.community_group_post_text);
        groupPostText =(TextView) view.findViewById(R.id.community_textview_group_post);
        togle_privacy =(ToggleButton) view.findViewById(R.id.community_privecy_text);
        communityPic = (MaskedImageView) view.findViewById(R.id.iv_community_pic);
        communityPic.setImageDrawable(getResources().getDrawable(R.drawable.background_round_color_second));
        textView_imageTitle = (TextView) view.findViewById(R.id.fragment_community_step2_textView_imageTitle);
        activateButterKnife(view);
        mListener.setCommunityPicReference(communityPic);
        togle_privacy.setOnClickListener(this);
        toggle_group.setOnClickListener(this);
        communityPic.setOnClickListener(this);
        setFonts();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setProfileData();
    }
    public void setProfileData(){
        if (community.location != null) {
            if (community.location.name != null);
        }
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void setFonts() {
        Typeface avenirNextRegular=Typeface.createFromAsset(getActivity().getAssets(), Constants.AvenirNextRegular);
        edit_community_name.setTypeface(avenirNextRegular);
        edit_community_description.setTypeface(avenirNextRegular);
        privacyText.setTypeface(avenirNextRegular);
        groupPostText.setTypeface(avenirNextRegular);
        toggle_group.setTypeface(avenirNextRegular);
        togle_privacy.setTypeface(avenirNextRegular);
        community_location.setTypeface(avenirNextRegular);
        button_next.setTypeface(avenirNextRegular);
        textView_imageTitle.setTypeface(avenirNextRegular);
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

    public void openLocationPopUp(){
        final Dialog confirmationDialog = new Dialog(getActivity());
        confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        confirmationDialog.setCanceledOnTouchOutside(false);
        confirmationDialog.setContentView(R.layout.location_popup);
        Button buttonDone;
        buttonDone = (Button) confirmationDialog.findViewById(R.id.popup_button_done);
        label = (TextView) confirmationDialog.findViewById(R.id.popup_text_add_location);
        enterLocation = (EditText) confirmationDialog.findViewById(R.id.popup_edittext_location);
        Typeface lato_regular = Typeface.createFromAsset(getActivity().getAssets(), Constants.AvenirNextRegular);
        buttonDone.setTypeface(lato_regular);
        label.setTypeface(lato_regular);
        enterLocation.setTypeface(lato_regular);
        confirmationDialog.show();
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enterLocation.getText().toString().isEmpty()){
                    ToastUtils.longToast("Enter Location");
                }else {

                    address = enterLocation.getText().toString();
                    //onLocationClick(address);
                    AddressUtil addressUtil = new AddressUtil();
                    addressUtil.urlConcat(address,asyncResultLocation);
                    if (community != null) {
                        community_location.setText(community.location.getName());
                    }
                    confirmationDialog.dismiss();
                }
            }
        });

    }
    AsyncResult<String> asyncResultLocation = new AsyncResult<String>() {
        @Override
        public void success(String locationName) {

            community.location.name = locationName;
        }

        @Override
        public void error(String error) {

        }
    };

    private void onLocationClick(String address){
        Intent intent = new Intent(getActivity(), GeoCodeIntentService.class);
        intent.putExtra(com.trutek.looped.geoCode.Constants.RECEIVER, mResultReceiver);
        int fetchType = com.trutek.looped.geoCode.Constants.USE_ADDRESS_NAME;
        intent.putExtra(com.trutek.looped.geoCode.Constants.FETCH_TYPE_EXTRA, fetchType);
        if(fetchType == com.trutek.looped.geoCode.Constants.USE_ADDRESS_NAME) {
            if(address.length() == 0) {
                ToastUtils.longToast("Please enter an address name");
                return;
            }
            intent.putExtra(com.trutek.looped.geoCode.Constants.LOCATION_NAME_DATA_EXTRA, address);
        }
        Log.e("General Fragment", "Starting Service");
        getActivity().startService(intent);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == communityPic.getId()){
            imagePickHelper.pickAnImage(this, ImageUtils.IMAGE_REQUEST_CODE);
            textView_imageTitle.setVisibility(View.GONE);
        }
        if(view.getId() == togle_privacy.getId()){
            String privacy_selected_text =togle_privacy.getText().toString();
            if(privacy_selected_text.contains("Privacy")){
                community.isPrivate =false;
            }else{
                community.isPrivate =true;
            }
        }
        if(view.getId() == toggle_group.getId()){
            String group_selected_text =toggle_group.getText().toString();
          if(group_selected_text.contains("Everyone")){
              community.canSeePost=true;
          }else{
              community.canSeePost=false;
          }
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void setCommunityPicReference(MaskedImageView communityPic);

        void CreateCommunity();
    }

    @OnClick(R.id.new_community_location_editText)
    public void location(){
        openLocationPopUp();
    }

    @OnClick(R.id.new_community_location_global_image_icon) public void global(){
        community.location = new LocationModel();
        community.location.name = getString(R.string.text_global);
        community_location.setText(getString(R.string.text_global));
    }

    @OnClick(R.id.new_community_location_private_image_icon) public void onLocationClick(){
        openLocationPopUp();
    }


    @OnClick(R.id.create_community_button_next)
    public void nextClick(){
        community.subject = edit_community_name.getText().toString();
        community.body = edit_community_description.getText().toString();
        if(community.location == null){
            ToastUtils.longToast(getString(R.string.empty_location));
            return;
        }else {
            community.location.name = community_location.getText().toString();
        }
        if(community.subject == null || community.subject.isEmpty()){
            ToastUtils.longToast(getString(R.string.community_subject));
            return;
        }
        else if(community.body == null || community.body.isEmpty()){
            ToastUtils.longToast(getString(R.string.community_body));
            return;
        }
        else
           mListener.CreateCommunity();
    }

    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == com.trutek.looped.geoCode.Constants.SUCCESS_RESULT) {
                final Address address = resultData.getParcelable(com.trutek.looped.geoCode.Constants.RESULT_ADDRESS);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         name = resultData.getString(com.trutek.looped.geoCode.Constants.RESULT_DATA_KEY);
                        if(address != null){
                            if(community != null){
                                community.location = new LocationModel();
                            }
                            community.location.coordinates.add(String.valueOf(address.getLongitude()));
                            community.location.coordinates.add(String.valueOf(address.getLatitude()));
                            community.location.description = name;
                            community.location.name = name;
                        }
                            community_location.setText(name);


                    }
                });
            }
            else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(R.string.location_error);
                    }
                });
            }
        }
    }


}
