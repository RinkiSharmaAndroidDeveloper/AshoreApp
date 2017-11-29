package com.trutek.looped.ui.authenticate;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//import com.facebook.login.widget.LoginButton;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.LoginModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.utils.KeyboardUtils;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.ValidationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpFragment extends BaseV4Fragment {

    @BindView(R.id.asu_text_or) TextView textViewOr;
    @BindView(R.id.asu_edit_text_phone_number) EditText phoneNumber;
    @BindView(R.id.asu_btn_verify) Button verify;
    @BindView(R.id.sign_in_facebook) TextView buttonFacebook;

    private LoginModel loginModel;

    private OnFragmentInteractionListener mListener;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance(LoginModel model) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();

        fragment.loginModel = model;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setupActivityComponent() {
    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this,view);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        activateButterKnife(view);
        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber.setCursorVisible(true);
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                phoneNumber.setCursorVisible(false);
                if(getActivity().getCurrentFocus()!=null && getActivity().getCurrentFocus() instanceof EditText){
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
                return true;
            }
        });

        setFonts();
        return view;

    }
    // TODO: Rename method, update argument and hook method into UI event
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

    @OnClick(R.id.asu_btn_verify)
    public void verify() {
        signUp();
    }

    @OnClick(R.id.sign_in_facebook)
    public void faceBookLogin() {
        ((SignUpActivity) getActivity()).facebookLogin();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void signUp() {
        KeyboardUtils.hideKeyboard(getActivity());
        String mobileNumber = phoneNumber.getText().toString();
        mobileNumber = "+"+ getCountryZipCode() + mobileNumber;
        if (new ValidationUtils(getActivity()).isMobileNumberValid(mobileNumber)) {
            loginModel.phone = mobileNumber;
            ((SignUpActivity) getActivity()).signUpWithMobile(loginModel);
        } else {
            ToastUtils.longToast(getString(R.string.auth_mobile_field_is_incorrect));
        }
    }

    private void setFonts() {
        Typeface avenirNextRegular=Typeface.createFromAsset(getActivity().getAssets(), Constants.AvenirNextRegular);
        textViewOr.setTypeface(avenirNextRegular);
        phoneNumber.setTypeface(avenirNextRegular);
        verify.setTypeface(avenirNextRegular);
        buttonFacebook.setTypeface(avenirNextRegular);
    }

    public String getCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }
}
