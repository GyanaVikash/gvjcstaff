package com.gvjc.gvjcstaff;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class HtmlActivity extends AppCompatActivity {
   ProgressBar progressBar;
LinearLayout progressBar_layout;
    private WebView mWebView;
    public SwipeRefreshLayout swipe;
    String url="https://help.gvhssb.in.net/tickets";
    public static String CurURL="";
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(this, "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
       progressBar = findViewById(R.id.progressBar);
        progressBar_layout=findViewById((R.id.progressBar_layout));
        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                WebAction(getCurrentURL(CurURL));
            }
        });
        WebAction(url);
    }

    public void WebAction(String U) {

        mWebView = findViewById(R.id.webview);

        mWebView.setWebViewClient(new WebViewClient());

        mWebView.loadUrl(U);


        mWebView.setInitialScale(50);
        WebSettings mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowContentAccess(true);

        mWebView.setWebChromeClient(new WebChromeClient() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();

                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);

                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
               progressBar.setVisibility(View.VISIBLE);
                progressBar_layout.setVisibility(View.VISIBLE);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                swipe.setRefreshing(false);
                super.onPageFinished(view, url);
                setCurrentURL(url);
                progressBar.setVisibility(View.GONE);
                progressBar_layout.setVisibility(view.GONE);

            }

        });



    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            if (getCurrentURL(CurURL).equals("https://help.gvhssb.in.net/")) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Exit Doubt Section");
                dialog.setMessage("Are you sure you want to exit ?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("EXIT Doubt Section", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                dialog.setCancelable(false);
                dialog.setNegativeButton("KEEP DOUBT CLEARING", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            } else {
                mWebView.goBack();
            }

        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Exit Doubt Section");
            dialog.setMessage("Are you sure you want to exit ?");
            dialog.setCancelable(false);
            dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.setCancelable(false);
            dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();

        }
    }

    public void setCurrentURL(String u) {
        CurURL = u;
    }

    public String getCurrentURL(String u) {
        return u;
    }
}
