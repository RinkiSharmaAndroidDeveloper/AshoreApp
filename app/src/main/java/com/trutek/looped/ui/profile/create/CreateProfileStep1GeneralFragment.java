package com.trutek.looped.ui.profile.create;

import android.app.Activity;
import android.app.Dialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.ResultReceiver;
import android.graphics.Typeface;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.trutek.looped.chatmodule.commands.QBLoadAttachFileCommand;
import com.trutek.looped.data.contracts.models.LocationModel;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.msas.common.Utils.ErrorUtils;
import com.trutek.looped.msas.common.Utils.ageCalculator.Age;
import com.trutek.looped.msas.common.Utils.ageCalculator.AgeCalculator;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.authenticate.SignupLocationCategoryActivity;
import com.trutek.looped.utils.DialogUtil;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.msas.common.helpers.DateHelper;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.geoCode.GeoCodeIntentService;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.utils.cloudinary.CloudinaryHelper;
import com.trutek.looped.utils.helpers.ImagePickHelper;
import com.trutek.looped.utils.image.ImageLoaderUtils;
import com.trutek.looped.utils.image.ImageUtils;
import com.trutek.looped.utils.listeners.OnImagePickedListener;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateProfileStep1GeneralFragment extends BaseV4Fragment implements View.OnClickListener {

    private static final int SECOND_ACTIVITY_RESULT_CODE = 1;

    @Inject
    IProfileService _profileService;
    @BindView(R.id.iv_group_image)
    MaskedImageView image_profile_pic;
    EditText editText_name,about;
    TextView textView_gender,textView_birthday;
    ImageView imageView_blur_background;
    LinearLayout linearLayoutInterest;
    private ProfileModel profile,profileModel;
    static final int REQUEST_CATEGORY = 1;
    private static final float BLUR_RADIUS = 25f;
 //   private AddressResultReceiver mResultReceiver;
    private double lat_value, long_value;

    private OnFragmentInteractionListener mListener;
    protected ImagePickHelper imagePickHelper;


    public CreateProfileStep1GeneralFragment() {
        // Required empty public constructor
    }

    public static CreateProfileStep1GeneralFragment newInstance(ProfileModel profile) {
        CreateProfileStep1GeneralFragment fragment = new CreateProfileStep1GeneralFragment();
        Bundle args = new Bundle();
        args.putSerializable("profile", profile);
        fragment.setArguments(args);
        return fragment;
    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // mResultReceiver = new AddressResultReceiver(null);
        profile = (ProfileModel) getArguments().getSerializable("profile");
        imagePickHelper = new ImagePickHelper();

}

    @Override
    protected void setupActivityComponent() {
        App.get(getActivity()).component().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_create_profile_signup, container, false);
        profileModel =new ProfileModel();
        editText_name =(EditText)view.findViewById(R.id.username_editText);
        textView_birthday =(TextView)view.findViewById(R.id.text_birthday);
        about =(EditText)view.findViewById(R.id.edit_text_about);
        textView_gender =(TextView)view.findViewById(R.id.text_gender);
        imageView_blur_background =(ImageView)view.findViewById(R.id.image_view_profile_blur_editProfile);
        linearLayoutInterest =(LinearLayout)view.findViewById(R.id.create_activity_linear_layout);
        textView_birthday.setOnClickListener(this);
        textView_gender.setOnClickListener(this);
        linearLayoutInterest.setOnClickListener(this);
        activateButterKnife(view);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mListener.setNameTextReference(editText_name);
        mListener.setProfilePicReference(image_profile_pic);
        editText_name.setImeOptions(EditorInfo.IME_ACTION_DONE);  // keypad done button

        mListener.setAboutUsReference(about);
        profileModel = _profileService.getMyProfile(null);
        about.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_DONE){
                    about.setCursorVisible(false);
                }
                return false;
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                about.setCursorVisible(true);
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                about.setCursorVisible(false);
                if ((getActivity().getCurrentFocus()) != null && (getActivity().getCurrentFocus()) instanceof EditText) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
                return true;
            }
        });


        editText_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_name.setCursorVisible(true);
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText_name.setCursorVisible(false);
                if (getActivity().getCurrentFocus() != null && getActivity().getCurrentFocus() instanceof EditText) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
                return true;
            }
        });
        editText_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editText_name.setCursorVisible(false);
                }
                return false;
            }
        });


        setFonts();
        return view;
    }

    private void displayImageByUrl(String publicUrl, ImageView imageView) {
        ImageLoader.getInstance().displayImage(publicUrl, imageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS, new ImageLoadingListener(imageView));
    }
    public Bitmap makeImageBlur(Bitmap bitmapImage){
        Bitmap outputBitmap = null;
        try {
            if (null == bitmapImage)
                return null;

            outputBitmap = Bitmap.createBitmap(bitmapImage);
            final RenderScript renderScript = RenderScript.create(getActivity().getApplicationContext());
            Allocation tmpIn = Allocation.createFromBitmap(renderScript, bitmapImage);
            Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);
            //Intrinsic Gausian blur filter
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);
            renderScript.destroy();
        }
        catch (Exception e){
            Log.d("Exception ","method exception "+e.getMessage());
        }
        return outputBitmap;
    }
    public class ImageLoadingListener extends SimpleImageLoadingListener {

        private Bitmap loadedImageBitmap;
        private String imageUrl;
        private MaskedImageView maskedImageView;
        private ImageView imageView;

        public ImageLoadingListener(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            super.onLoadingStarted(imageUri, view);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            imageUrl = null;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, final Bitmap loadedBitmap) {
            initMaskedImageView(loadedBitmap);
            this.imageUrl = imageUri;
        }

        private void initMaskedImageView(Bitmap bitmap_blur) {
            Bitmap blurred_image = makeImageBlur(bitmap_blur);
            imageView.setImageBitmap(blurred_image);
        }

    }

    private void setFonts() {
        Typeface avenirNextRegular = Typeface.createFromAsset(getActivity().getAssets(), Constants.AvenirNextRegular);
        textView_birthday.setTypeface(avenirNextRegular);
        textView_gender.setTypeface(avenirNextRegular);
        about.setTypeface(avenirNextRegular);
        editText_name.setTypeface(avenirNextRegular);
    }

    @OnClick(R.id.iv_group_image)
    public void galleryImage() {
        imagePickHelper.pickAnImage(this, ImageUtils.IMAGE_REQUEST_CODE);
    }


        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case (R.id.text_birthday):
                    about.setCursorVisible(false);
                    datePicker();
                    break;
                case (R.id.text_gender):
                    DialogUtil.showGenderDialog(getActivity(),genderDialogCallback);
                    about.setCursorVisible(false);
                    break;
                case (R.id.create_activity_linear_layout):
                   Intent intent =new Intent(getActivity().getApplicationContext(), SignupLocationCategoryActivity.class);
                    intent.putExtra(Constants.MODEL_PROFILE,profile);
                    startActivityForResult(intent,REQUEST_CATEGORY);
                    break;

            }

        }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CATEGORY && resultCode == getActivity().RESULT_OK){
            if(null != data){
                profile = (ProfileModel) data.getSerializableExtra(Constants.MODEL_PROFILE);
            }
        }
    }

  /*  public void popupOpen() {
        final Dialog confirmationDialog = new Dialog(getActivity());
        confirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmationDialog.setContentView(R.layout.location_popup);
        Button buttonDone;
        final TextView textAddlocation;
        final EditText editText_location_popup;
        buttonDone = (Button) confirmationDialog.findViewById(R.id.popup_button_done);
        textAddlocation = (TextView) confirmationDialog.findViewById(R.id.popup_text_add_location);
        editText_location_popup = (EditText) confirmationDialog.findViewById(R.id.popup_edittext_location);
        Typeface lato_regular = Typeface.createFromAsset(getActivity().getAssets(), Constants.AvenirNextRegular);
        buttonDone.setTypeface(lato_regular);
        textAddlocation.setTypeface(lato_regular);
        editText_location_popup.setTypeface(lato_regular);
        confirmationDialog.show();
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                if (editText_location_popup.getText().toString().isEmpty()) {
                    ToastUtils.longToast("Enter Location");
                } else {
                    String address = editText_location_popup.getText().toString();
                    onLocationClick(address);
                    confirmationDialog.dismiss();

                }


            }
        });*/
       /* confirmationDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (getActivity().getCurrentFocus() != null && getActivity().getCurrentFocus() instanceof EditText) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
            }
        });
    }*/

   /* @OnClick(R.id.general_textview_location)
    public void showPopUp() {
        popupOpen();
    }
*/
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void setNameTextReference(EditText editText);
        void setAboutUsReference(EditText editText);

        void setProfilePicReference(MaskedImageView imageView);
    }

 /*   @OnClick(R.id.text_gender)
    public void gender() {

        DialogUtil.showGenderDialog(getActivity(),genderDialogCallback);

    }*/

  //  @OnClick(R.id.text_birthday)
    public void datePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                if (DateHelper.getDate(year, monthOfYear, dayOfMonth).getTime() > System.currentTimeMillis()) {
                    ToastUtils.shortToast("Make sure you birth date is in the past.");
                } else {
                    profile.dateOfBirth = DateHelper.getDate(year, monthOfYear, dayOfMonth);
                    Age age = AgeCalculator.calculateAge(profile.dateOfBirth);
                    profile.age = age.getYears();
                    textView_birthday.setText(DateHelper.stringify(profile.dateOfBirth));
                }
            }
        }, year, month, day);
        mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        mDatePicker.show();
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
    public void onResume() {
        super.onResume();
     //   setProfileData();
    }

   /* private void onLocationClick(String address) {
        Intent intent = new Intent(getActivity(), GeoCodeIntentService.class);
        intent.putExtra(com.trutek.looped.geoCode.Constants.RECEIVER, mResultReceiver);
        int fetchType = com.trutek.looped.geoCode.Constants.USE_ADDRESS_NAME;
        intent.putExtra(com.trutek.looped.geoCode.Constants.FETCH_TYPE_EXTRA, fetchType);
        if (fetchType == com.trutek.looped.geoCode.Constants.USE_ADDRESS_NAME) {
            if (address.length() == 0) {
                ToastUtils.longToast("Please enter an address name");
                return;
            }
            intent.putExtra(com.trutek.looped.geoCode.Constants.LOCATION_NAME_DATA_EXTRA, address);
        }
        Log.e("General Fragment", "Starting Service");
        getActivity().startService(intent);
    }*/

   /* class AddressResultReceiver extends ResultReceiver {

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
                        String name = resultData.getString(com.trutek.looped.geoCode.Constants.RESULT_DATA_KEY);
                        if (address != null) {
                            profile.location = new LocationModel();
                            profile.location.coordinates.add(String.valueOf(address.getLongitude()));
                            profile.location.coordinates.add(String.valueOf(address.getLatitude()));
                            profile.location.description = name;
                            profile.location.name = name;
                        }
                        textView_location.setText(name);
                    }
                });
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(R.string.location_error);
                    }
                });
            }
        }
    }*/

    private void setProfileData() {
        if (profile.name != null)
            editText_name.setText(profile.name);
        if (profile.gender != null)
            textView_gender.setText(profile.gender);
        if (profile.dateOfBirth != null)
            textView_birthday.setText(DateHelper.stringify(profile.dateOfBirth));
        /*if (profile.location != null) {
            if (profile.location.name != null)
                textView_location.setText(profile.location.name);
        }*/
    }

    AsyncResult<String> genderDialogCallback=new AsyncResult<String>() {
        @Override
        public void success(String gender) {
            textView_gender.setText(gender);
            profile.gender = gender;
        }

        @Override
        public void error(String error) {

        }
    };

}
