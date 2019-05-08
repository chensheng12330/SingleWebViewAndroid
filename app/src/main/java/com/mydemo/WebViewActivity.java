package com.mydemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;


/**
 *
 */
public class WebViewActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "WebViewActivity";
    private MyScrollWebView mWv;
    private ValueCallback<Uri> mUploadFile;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private WebViewActivity.PopupWindows mPopupWindow;
    private final int REQUEST_CODE_TAKE = 100;
    private final int REQUEST_CODE_PICK = 200;
    private static final int PERMISSION_CAMERA = 103;
    private Uri mFileUri;
    private PtrClassicFrameLayout mRefreshLayout;
    private RefreshListViewHeader mRefreshListViewHeader;
    private String mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUrl = "http://www.pz5918.com/wap";

        mWv = (MyScrollWebView) findViewById(R.id.wv);
        mRefreshLayout = (PtrClassicFrameLayout) findViewById(R.id.refresh_layout);
        initRefreshLayout();
        mWv.setFocusableInTouchMode(true);
        mWv.setFocusable(true);
        mWv.requestFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webSetting(mWv.getSettings());
        setDownloadListener();
        initEvent();
        mWv.loadUrl(mUrl);
    }

    private void initRefreshLayout() {
        if (mRefreshListViewHeader == null) {
            mRefreshListViewHeader = new RefreshListViewHeader(this);
        }
        mRefreshLayout.setHeaderView(mRefreshListViewHeader);
        mRefreshLayout.addPtrUIHandler(mRefreshListViewHeader);
        mRefreshLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {

                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWv.loadUrl(mUrl);
                        mRefreshLayout.refreshComplete();

                    }
                }, 1000);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                //判断当前是否可以进行下拉刷新
                if (mWv != null) {
                    if (mWv.isReadyForPullStart()) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void setDownloadListener() {
        mWv.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                // H5中包含下载链接的话让外部浏览器去处理
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void webSetting(WebSettings settings) {
        settings.setAllowFileAccess(true);
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(false);
        settings.setDatabaseEnabled(false);
        settings.setSupportZoom(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSavePassword(true);
        settings.setSaveFormData(true);
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setGeolocationEnabled(true);
        settings.setGeolocationDatabasePath(dir);
        settings.setDomStorageEnabled(true);
        settings.setTextZoom(100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //下面两句代码，解决h5页面加载网络图片失败的问题
        settings.setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);


    }


    protected void initEvent() {
        mWv.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setTitle(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            // For Lollipop 5.0+ Devices
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mUploadCallbackAboveL != null) {
                    mUploadCallbackAboveL.onReceiveValue(null);
                }
                mUploadCallbackAboveL = filePathCallback;

                 mPopupWindow = new WebViewActivity.PopupWindows(WebViewActivity.this);
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

            // For Android >= 3.0
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {

                if (mUploadFile != null) {
                    mUploadFile.onReceiveValue(null);
                }
                mUploadFile = uploadMsg;

                mPopupWindow = new WebViewActivity.PopupWindows(WebViewActivity.this);



            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");

            }
        });
        mWv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                 Log.i(TAG, "shouldOverrideUrlLoading url:" + url);
                return false;
            }

            //忽略证书验证
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                Log.i(TAG, "onReceivedSslError ");
            }


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e(TAG, "onReceivedError " + errorCode + "==" + description + "==" + failingUrl);
                Toast.makeText(getApplicationContext(), "加载失败,请检测您的网络或尝试下拉刷新.", Toast.LENGTH_LONG).show();
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            releaseReceiveValue();
        }else if (mWv.canGoBack()) {
            mWv.goBack();
        } else {
           super.onBackPressed();
        }
    }

    private boolean requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            return false;
        }
        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TAKE) {
            // 调用相机拍照
            if (resultCode != RESULT_OK) {
                mFileUri = null;
            }
            postFile(mFileUri);
        } else if (requestCode == REQUEST_CODE_PICK) {
            // 直接从相册获取
            Uri uri = data == null ? null : data.getData();
            postFile(uri);

        }

    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




    @Override
    public void onClick(View v) {
    }

    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.input_file_pop, null);
            //view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.popshow_anim));
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setOutsideTouchable(false);
            setFocusable(false);
            this.update();
            setContentView(view);
            showAtLocation(mWv, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

            TextView takePhotoTv = (Button) view.findViewById(R.id.tv_take_photo);
            TextView pickPhotoTv = (Button) view.findViewById(R.id.tv_pick_photo);
            TextView cancelBtn = (Button) view.findViewById(R.id.tv_cancel);
            takePhotoTv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //拍照
                    takePhoto();
                    dismiss();
                }
            });
            pickPhotoTv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //选择相片
                    pickPhoto();
                    dismiss();
                }
            });
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    releaseReceiveValue();
                    dismiss();
                }
            });

        }
    }

    private void postFile(Uri uri) {
        if (mUploadFile != null) {
            mUploadFile.onReceiveValue(uri);
            mUploadFile = null;
        }
        if (mUploadCallbackAboveL != null) {
            if (uri == null) {
                mUploadCallbackAboveL.onReceiveValue(null);
            } else {
                mUploadCallbackAboveL.onReceiveValue(new Uri[]{uri});
            }
            mUploadCallbackAboveL = null;
        }
    }

    private Uri getFileUri(Context context) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/mmt/" + System.currentTimeMillis() + ".jpg");
        file.getParentFile().mkdirs();
        Uri mUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mUri = FileProvider.getUriForFile(context, "com.mmt.market.fileprovider", file);
        } else {
            mUri = Uri.fromFile(file);
        }
        return mUri;
    }

    private void takePhoto() {
        if (requestPermission()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mFileUri = getFileUri(this);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
            startActivityForResult(intent, REQUEST_CODE_TAKE);
        } else {
            releaseReceiveValue();
        }
    }

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK);
    }

    private void releaseReceiveValue() {
        if (mUploadFile != null) {
            mUploadFile.onReceiveValue(null);
            mUploadFile = null;
        }
        if (mUploadCallbackAboveL != null) {
            mUploadCallbackAboveL.onReceiveValue(null);
            mUploadCallbackAboveL = null;
        }
    }
}
