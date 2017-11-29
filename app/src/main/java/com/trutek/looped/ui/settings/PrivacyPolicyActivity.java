package com.trutek.looped.ui.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.ui.base.BaseAppCompatActivity;

/**
 * Created by Rinki on 3/15/2017.
 */
public class PrivacyPolicyActivity extends BaseAppCompatActivity implements View.OnClickListener{
    ImageView imageView;
    @Override
    protected int getContentResId() {
        return R.layout.activity_privacy_policy_setting;
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        imageView =(ImageView)findViewById(R.id.privacy_back);
        imageView.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.privacy_back):
                finish();
                break;
        }
    }

}
