package com.mydemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载启动图片
        setContentView(R.layout.launch);


        Handler handler = new Handler(Looper.myLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //跳转至 MainActivity
                Intent intent = new Intent(LaunchActivity.this, WebViewActivity.class);
                startActivity(intent);
                //结束当前的 Activity
                LaunchActivity.this.finish();
            }
        },1000);

    }
}

/*
*   Handler handler = new Handler(Looper.myLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //跳转至 MainActivity
                Intent intent = new Intent(LaunchActivity.this, WebViewActivity.class);
                startActivity(intent);
                //结束当前的 Activity
                LaunchActivity.this.finish();
            }
        },1000);
*
* */