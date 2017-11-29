package com.trutek.looped.utils.helpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.trutek.looped.utils.imagepicker.ImagePickHelperFragment;
import com.trutek.looped.utils.imagepicker.ImageSourcePickDialogFragment;

public class ImagePickHelper {

    public void pickAnImage(Fragment fragment, int requestCode) {
        ImagePickHelperFragment imagePickHelperFragment = ImagePickHelperFragment
                .start(fragment, requestCode);
        showImageSourcePickerDialog(fragment.getChildFragmentManager(), imagePickHelperFragment);
    }

    public void pickAnImage(FragmentActivity activity, int requestCode) {
        ImagePickHelperFragment imagePickHelperFragment = ImagePickHelperFragment
                .start(activity, requestCode);
        showImageSourcePickerDialog(activity.getSupportFragmentManager(), imagePickHelperFragment);
    }

    private void showImageSourcePickerDialog(FragmentManager fm, ImagePickHelperFragment fragment) {
        ImageSourcePickDialogFragment.show(fm,
                new ImageSourcePickDialogFragment.LoggableActivityImageSourcePickedListener(fragment));
    }
}