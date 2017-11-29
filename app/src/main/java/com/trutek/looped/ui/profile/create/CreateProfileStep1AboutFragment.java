package com.trutek.looped.ui.profile.create;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.ProfileModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateProfileStep1AboutFragment extends BaseV4Fragment {

    @BindView(R.id.edit_text_about) EditText aboutUs;

    private OnFragmentInteractionListener mListener;
    private ProfileModel profile;

    public CreateProfileStep1AboutFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CreateProfileStep1AboutFragment newInstance(ProfileModel profile) {
        CreateProfileStep1AboutFragment fragment = new CreateProfileStep1AboutFragment();
        Bundle args = new Bundle();
        args.putSerializable("profile", profile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profile = (ProfileModel) getArguments().getSerializable("profile");
    }

    private void activateButterKnife(View view) {
        ButterKnife.bind(this,view);
    }

    @Override
    protected void setupActivityComponent() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        activateButterKnife(view);
        mListener.setAboutUsReference(aboutUs);

        aboutUs.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_DONE){
                    aboutUs.setCursorVisible(false);
                }
                return false;
            }
        });
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutUs.setCursorVisible(true);
            }
        });
       view.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               aboutUs.setCursorVisible(false);
               if ((getActivity().getCurrentFocus()) != null && (getActivity().getCurrentFocus()) instanceof EditText) {
                   InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                   imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
               }
               return true;
           }
       });

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void setAboutUsReference(EditText editText);
    }
}
