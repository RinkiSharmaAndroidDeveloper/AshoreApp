package com.trutek.looped.ui.customcontrol;

import android.content.Context;
import android.util.AttributeSet;

import com.trutek.looped.msas.common.views.maskedimageview.MaskedImageView;

/**
 * Created by Amrit on 06/02/17.
 */
public class CustomMaskImageView extends MaskedImageView {
    public CustomMaskImageView(Context context) {
        super(context);
    }

    public CustomMaskImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, (int)(widthMeasureSpec * 0.2));
    }
}
