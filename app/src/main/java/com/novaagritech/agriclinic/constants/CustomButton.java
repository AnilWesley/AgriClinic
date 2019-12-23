package com.novaagritech.agriclinic.constants;

import android.content.Context;
import android.graphics.Typeface;

import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

public class CustomButton extends AppCompatButton {
    public CustomButton(Context context) {
        super(context);
        setTypeface(context);
    }

    public CustomButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setTypeface(context);
    }

    public CustomButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setTypeface(context);
    }

    private void setTypeface(Context context) {
        setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/NotoSans-Regular.ttf"));
    }
}
