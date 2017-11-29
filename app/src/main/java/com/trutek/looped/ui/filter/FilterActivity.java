package com.trutek.looped.ui.filter;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;
import com.trutek.looped.R;
import com.trutek.looped.msas.common.Utils.Constants;
import com.trutek.looped.ui.base.BaseAppCompatActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class FilterActivity extends BaseAppCompatActivity{

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.filter_edittext_interest)EditText edit_interest;
    @BindView(R.id.filter_edittext_topics)EditText edit_topics;
    @BindView(R.id.filter_edittext_location)EditText edit_location;
    @BindView(R.id.filter_button_show_result)Button button_show_result;
    @BindView(R.id.text_filter)TextView text_filter;

    @Override
    protected int getContentResId() {
        return R.layout.activity_filter;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFonts();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }

    }

    private void setFonts() {
        Typeface avenirNextRegular=Typeface.createFromAsset(getAssets(), Constants.AvenirNextRegular);
        edit_interest.setTypeface(avenirNextRegular);
        edit_topics.setTypeface(avenirNextRegular);
        edit_location.setTypeface(avenirNextRegular);
        button_show_result.setTypeface(avenirNextRegular);
        text_filter.setTypeface(avenirNextRegular);
    }

    @Override
    protected void setupActivityComponent() {

    }

    @OnClick(R.id.filter_button_show_result)
    public void showResult(){
        Toast.makeText(getApplicationContext(),"ComingSoon",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
