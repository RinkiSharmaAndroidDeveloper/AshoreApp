package com.trutek.looped.ui.dialogs;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;
import com.trutek.looped.ui.base.BaseV4Fragment;
import com.trutek.looped.utils.image.ImageLoaderUtils;
import com.trutek.looped.utils.image.ImageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewImageFragment extends DialogFragment {

    @BindView(R.id.image_view) ImageView profilePic;

    private OnFragmentInteractionListener mListener;
    private String imageUrl;

    public PreviewImageFragment() {
        // Required empty public constructor
    }

    public static PreviewImageFragment newInstance(String imageUrl) {
        PreviewImageFragment fragment = new PreviewImageFragment();
        Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString("imageUrl");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_preview_image, container, false);
        activateButterKnife(view);

        initView();
        return view;
    }

    private void initView() {
        if(imageUrl != null && !imageUrl.isEmpty() && imageUrl.contains("http")){
            displayMaskedImageByUrl(imageUrl, profilePic);
        }
    }

    private void displayMaskedImageByUrl(String publicUrl, ImageView maskedImageView) {
        ImageLoader.getInstance().displayImage(publicUrl,maskedImageView,
                ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS);
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

    private void activateButterKnife(View view) {
        ButterKnife.bind(this,view);
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
}
