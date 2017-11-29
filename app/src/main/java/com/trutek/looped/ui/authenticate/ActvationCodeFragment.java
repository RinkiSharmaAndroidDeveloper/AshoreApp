package com.trutek.looped.ui.authenticate;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.LoginModel;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.ValidationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class  ActvationCodeFragment extends Fragment {

    @BindView(R.id.activation_edit_text_activated_code) EditText activationCode;
    @BindView(R.id.activation_btn_activate) Button activate;

    private LoginModel loginModel;
    private OnFragmentInteractionListener mListener;

    public ActvationCodeFragment() {
        // Required empty public constructor
    }

    public static ActvationCodeFragment newInstance(LoginModel model) {
        ActvationCodeFragment fragment = new ActvationCodeFragment();
        Bundle args = new Bundle();

        fragment.loginModel = model;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this,view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_actvation_code, container, false);
        activateButterKnife(view);

       view.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               activationCode.setCursorVisible(false);
               if(getActivity().getCurrentFocus()!=null && getActivity().getCurrentFocus() instanceof EditText){
                   InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                   imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
               }
               return true;

           }
       });

        activationCode.setImeOptions(EditorInfo.IME_ACTION_DONE);
        activationCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId== EditorInfo.IME_ACTION_DONE){
                    activationCode.setCursorVisible(false);
                }
                return false;
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @OnClick(R.id.activation_edit_text_activated_code)
     public  void  showCursor(){
        activationCode.setCursorVisible(true);
    }

    @OnClick(R.id.activation_btn_activate)
    public void activateCode(){
        String code = activationCode.getText().toString();
        activationCode.setCursorVisible(false);
        if(getActivity().getCurrentFocus()!=null && getActivity().getCurrentFocus() instanceof EditText){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
        if (new ValidationUtils(getActivity()).isPinValid(code)){
            loginModel.pin = code;
            ((SignUpActivity) getActivity()).validatePin(loginModel);
        } else {
            ToastUtils.longToast("Invalid Pin.");
        }
    }

    private void setFonts() {
        Typeface avenirNextRegular=Typeface.createFromAsset(getActivity().getAssets(), Constants.AvenirNextRegular);
        activate.setTypeface(avenirNextRegular);
        activationCode.setTypeface(avenirNextRegular);
    }

}
