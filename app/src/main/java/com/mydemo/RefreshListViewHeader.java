package com.mydemo;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * 下拉刷新的头部
 */
public class RefreshListViewHeader extends FrameLayout implements PtrUIHandler {

    private AnimationDrawable mAnim;

    public RefreshListViewHeader(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        View view = View.inflate(context, R.layout.header_refresh_listview, this);
        ImageView loading = (ImageView) view.findViewById(R.id.anmi_loading_person);
        mAnim = (AnimationDrawable) loading.getBackground();
        mAnim.start();
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        mAnim.start();
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        mAnim.stop();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

    }
}
