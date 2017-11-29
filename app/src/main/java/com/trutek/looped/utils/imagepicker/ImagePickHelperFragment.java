package com.trutek.looped.utils.imagepicker;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.system.ErrnoException;
import android.widget.Toast;

import com.trutek.looped.chatmodule.tasks.GetFilepathFromUriTask;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.utils.ToastUtils;
import com.trutek.looped.utils.image.ImageUtils;
import com.trutek.looped.utils.listeners.OnImagePickedListener;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImagePickHelperFragment extends Fragment {

    private static final String ARG_REQUEST_CODE = "requestCode";
    private static final String ARG_PARENT_FRAGMENT = "parentFragment";

    private static final String TAG = ImagePickHelperFragment.class.getSimpleName();

    private OnImagePickedListener listener;
    private Intent data;

    public ImagePickHelperFragment() {
    }

    public static ImagePickHelperFragment start(Fragment fragment, int requestCode) {
        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putString(ARG_PARENT_FRAGMENT, fragment.getClass().getSimpleName());

        return start(fragment.getActivity().getSupportFragmentManager(), args);
    }

    public static ImagePickHelperFragment start(FragmentActivity activity, int requestCode) {
        Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_CODE, requestCode);

        return start(activity.getSupportFragmentManager(), args);
    }

    private static ImagePickHelperFragment start(FragmentManager fm, Bundle args) {
        ImagePickHelperFragment fragment = (ImagePickHelperFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new ImagePickHelperFragment();
            fm.beginTransaction().add(fragment, TAG).commitAllowingStateLoss();
            fragment.setArguments(args);
        }
        return fragment;
    }

    public static void stop(FragmentManager fm) {
        Fragment fragment = fm.findFragmentByTag(TAG);
        if (fragment != null) {
            fm.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isResultFromImagePick(requestCode, resultCode, data)) {
            if (requestCode == ImageUtils.CAMERA_REQUEST_CODE && (data == null || data.getData() == null)) {
                // Hacky way to get EXTRA_OUTPUT param to work.
                // When setting EXTRA_OUTPUT param in the camera intent there is a chance that data will return as null
                // So we just pass fetchMyCommunities camera file as a data, because RESULT_OK means that photo was written in the file.
                data = new Intent();
                data.setData(Uri.fromFile(ImageUtils.getLastUsedCameraFile()));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                this.data = data;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                return;
            }

            new GetFilepathFromUriTask(getChildFragmentManager(), listener,
                    getArguments().getInt(ARG_REQUEST_CODE)).execute(data);

        } else {
            stop(getChildFragmentManager());
            if (listener != null) {
                listener.onImagePickClosed(getArguments().getInt(ARG_REQUEST_CODE));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (data != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            new GetFilepathFromUriTask(getChildFragmentManager(), listener,
                    getArguments().getInt(ARG_REQUEST_CODE)).execute(data);
        } else {
            ToastUtils.longToast("Required permissions are not granted");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Fragment fragment = ((BaseAppCompatActivity) activity).getSupportFragmentManager()
                .findFragmentByTag(getArguments().getString(ARG_PARENT_FRAGMENT));
        if (fragment != null) {
            if (fragment instanceof OnImagePickedListener) {
                listener = (OnImagePickedListener) fragment;
            }
        } else {
            if (activity instanceof OnImagePickedListener) {
                listener = (OnImagePickedListener) activity;
            }
        }

        if (listener == null) {
            throw new IllegalStateException(
                    "Either activity or fragment should implement OnImagePickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void setListener(OnImagePickedListener listener) {
        this.listener = listener;
    }

    private boolean isResultFromImagePick(int requestCode, int resultCode, Intent data) {
        Boolean isImagePicked = false;
        if (resultCode == Activity.RESULT_OK && ((requestCode == ImageUtils.CAMERA_REQUEST_CODE) || (requestCode == ImageUtils.GALLERY_REQUEST_CODE && data != null))) {
            isImagePicked = true;

        } else {
            isImagePicked = false;
        }
        return isImagePicked;
    }

    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getActivity().getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
}