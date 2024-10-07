package com.uan.brainmher.application.ui.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

public class CustomViewPager extends FrameLayout {

    private ViewPager2 viewPager;
    private boolean enabled = true;

    public CustomViewPager(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        viewPager = new ViewPager2(context);
        addView(viewPager);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return enabled && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return enabled && super.onTouchEvent(ev);
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ViewPager2 getViewPager() {
        return viewPager;
    }
}