package com.trutek.looped.ui.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.ui.base.BaseAppCompatActivity;

/**
 * Created by Rinki on 3/15/2017.
 */
public class TermsActivity extends BaseAppCompatActivity implements View.OnClickListener {
    ImageView back_image;

    @Override
    protected int getContentResId() {
        return R.layout.activity_terms_setting;
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        back_image = (ImageView) findViewById(R.id.terms_back);
        back_image.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.terms_back):
                finish();
                break;
        }
    }
}
