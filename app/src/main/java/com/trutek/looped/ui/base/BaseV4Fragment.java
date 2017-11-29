package com.trutek.looped.ui.base;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.trutek.looped.msas.common.Utils.Constants;

import com.quickblox.chat.QBChatService;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.AppSession;
import com.trutek.looped.msas.common.net.NetworkDetector;
import com.trutek.looped.utils.ToastUtils;

public abstract class BaseV4Fragment extends Fragment {

    protected Typeface avenirNextRegular;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        avenirNextRegular=Typeface.createFromAsset(getActivity().getAssets(), Constants.AvenirNextRegular);
        setupActivityComponent();
    }


    protected abstract void setupActivityComponent();

    public boolean checkNetworkAvailableWithError() {
        if (!isNetworkAvailable()) {
            ToastUtils.longToast(R.string.dlg_fail_connection);
            return false;
        } else {
            return true;
        }
    }

    public boolean isNetworkAvailable() {
        return NetworkDetector.isNetworkAvailable(getActivity());
    }

    protected boolean isChatInitializedAndUserLoggedIn() {
        return isChatInitialized() && QBChatService.getInstance().isLoggedIn();
    }

    protected boolean isChatInitialized() {
        return QBChatService.getInstance().isLoggedIn() && AppSession.getSession().isSessionExist();
    }
}
