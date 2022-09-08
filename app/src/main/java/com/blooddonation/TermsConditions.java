package com.blooddonation;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

;

public class TermsConditions extends BaseActivity {

    private WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
        mWebview = new WebView(this);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.setOnLongClickListener(v -> true);
        mWebview.setLongClickable(false);// enable javascript
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(TermsConditions.this, description, Toast.LENGTH_SHORT).show();
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });
        mWebview.loadUrl("https://mobileappsdevblog.blogspot.com/2022/09/privacy-policy-and-terms-and-conditions.html");
        setContentView(mWebview);
    }

    public void back(View view) {
        finish();
    }
}
