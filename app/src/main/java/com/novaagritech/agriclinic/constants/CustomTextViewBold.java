package com.novaagritech.agriclinic.constants;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class CustomTextViewBold extends AppCompatTextView {
    public CustomTextViewBold(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setTypeface(context);
    }

    public CustomTextViewBold(Context context) {
        super(context);
        setTypeface(context);
    }

    public CustomTextViewBold(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setTypeface(context);
    }

    private void setTypeface(Context context) {
        setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/NotoSans-Bold.ttf"));
    }
}
