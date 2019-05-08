package com.mydemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * author：di.gong on 2017/9/27 16:32.
 */

public class MyScrollWebView extends WebView {
    private boolean isTop = false;

    public MyScrollWebView(Context context) {
        super(context);
    }

    public MyScrollWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        if (scrollY <= 0) {
            if (!isTop && deltaY < 0) {
                isTop = true;
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }


    public boolean isTop() {
        return isTop;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTop = false;
                break;
            case MotionEvent.ACTION_UP:
                isTop = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    public boolean isReadyForPullStart() {
        return getScrollY() == 0 && isTop();
    }
}
