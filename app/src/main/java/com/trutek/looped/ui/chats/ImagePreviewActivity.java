package com.trutek.looped.ui.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.trutek.looped.R;
import com.trutek.looped.chatmodule.ui.base.BaseLoggableActivity;
import com.trutek.looped.msas.common.views.TouchImageView;
import com.trutek.looped.utils.image.ImageLoaderUtils;

import butterknife.BindView;

public class ImagePreviewActivity extends BaseLoggableActivity {

    public static final String EXTRA_IMAGE_URL = "image";
    private static final int IMAGE_MAX_ZOOM = 4;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.image_touchimageview) TouchImageView imageTouchImageView;

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putExtra(EXTRA_IMAGE_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_image_preview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initFields();
        initTouchImageView();
        displayImage();
    }

    private void initFields() {
        title = getString(R.string.preview_image_title);
    }

    @Override
    protected void setupActivityComponent() {

    }

    private void displayImage() {
        String imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        if (!TextUtils.isEmpty(imageUrl)) {
            ImageLoader.getInstance().displayImage(imageUrl, imageTouchImageView,
                    ImageLoaderUtils.UIL_DEFAULT_DISPLAY_OPTIONS);
        }
    }

    private void initTouchImageView() {
        imageTouchImageView.setMaxZoom(IMAGE_MAX_ZOOM);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
