package com.ghsoft.android.dmz;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SwipeViewPager extends ViewPager {
    private boolean mIsEnabled;


    public SwipeViewPager(Context context, AttributeSet attrs) {

        super(context, attrs);

        mIsEnabled = true;

    }


    public SwipeViewPager(Context context) {

        super(context);

        mIsEnabled = true;

    }


    @Override

    public boolean onInterceptTouchEvent(MotionEvent event) {

        if (mIsEnabled) {

            return super.onInterceptTouchEvent(event);

        } else {

            return false;

        }

    }


    @Override

    public boolean onTouchEvent(MotionEvent event) {

        if (mIsEnabled) {

            return super.onTouchEvent(event);

        } else {

            return false;

        }

    }


    public void setPagingEnabled(boolean enabled) {

        this.mIsEnabled = enabled;

    }


}
