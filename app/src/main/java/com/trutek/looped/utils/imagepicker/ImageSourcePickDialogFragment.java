package com.trutek.looped.utils.imagepicker;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;

import com.trutek.looped.R;
import com.trutek.looped.chatmodule.ui.base.BaseLoggableActivity;
import com.trutek.looped.utils.image.ImageUtils;

public class ImageSourcePickDialogFragment extends DialogFragment {

    private static final int POSITION_GALLERY = 0;
    private static final int POSITION_CAMERA = 1;

    private OnImageSourcePickedListener onImageSourcePickedListener;

    public static void show(FragmentManager fragmentManager, OnImageSourcePickedListener onImageSourcePickedListener) {
        ImageSourcePickDialogFragment fragment = new ImageSourcePickDialogFragment();
        fragment.setOnImageSourcePickedListener(onImageSourcePickedListener);
        fragment.show(fragmentManager, ImageSourcePickDialogFragment.class.getSimpleName());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dlg_choose_image_from);
        builder.setItems(R.array.dlg_image_pick, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case POSITION_GALLERY:
                        onImageSourcePickedListener.onImageSourcePicked(ImageSource.GALLERY);
                        break;
                    case POSITION_CAMERA:
                        onImageSourcePickedListener.onImageSourcePicked(ImageSource.CAMERA);
                        break;
                }
            }
        });

        return builder.create();
    }

    public void setOnImageSourcePickedListener(OnImageSourcePickedListener onImageSourcePickedListener) {
        this.onImageSourcePickedListener = onImageSourcePickedListener;
    }

    public static enum ImageSource {
        GALLERY,
        CAMERA
    }

    public static interface OnImageSourcePickedListener {

        void onImageSourcePicked(ImageSource source);
    }

    public static class LoggableActivityImageSourcePickedListener implements OnImageSourcePickedListener {

        private Activity activity;
        private Fragment fragment;

        public LoggableActivityImageSourcePickedListener(Activity activity) {
            this.activity = activity;
        }

        public LoggableActivityImageSourcePickedListener(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onImageSourcePicked(ImageSource source) {
            switch (source) {
                case GALLERY:
                    if (fragment != null) {
                        Activity activity = fragment.getActivity();
                        setupActivityToBeNonLoggable(activity);
                        ImageUtils.startImagePicker(fragment);
                    } else {
                        setupActivityToBeNonLoggable(activity);
                        ImageUtils.startImagePicker(activity);
                    }
                    break;
                case CAMERA:
                    if (fragment != null) {
                        Activity activity = fragment.getActivity();
                        setupActivityToBeNonLoggable(activity);
                        ImageUtils.startCameraForResult(fragment);
                    } else {
                        setupActivityToBeNonLoggable(activity);
                        ImageUtils.startCameraForResult(activity);
                    }
                    break;
            }
        }

        private void setupActivityToBeNonLoggable(Activity activity) {
            if (activity instanceof BaseLoggableActivity) {
                BaseLoggableActivity loggableActivity = (BaseLoggableActivity) activity;
                loggableActivity.canPerformLogout.set(false);
            }
        }
    }
}