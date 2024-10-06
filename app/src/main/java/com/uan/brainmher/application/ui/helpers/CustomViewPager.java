package com.uan.brainmher.application.ui.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {

    private boolean enabled = false;

    public CustomViewPager(@NonNull Context context) {
        super(context);
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return this.enabled && super.onTouchEvent(ev) && performClick();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.enabled && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean performClick() {
        return this.enabled && super.performClick();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}