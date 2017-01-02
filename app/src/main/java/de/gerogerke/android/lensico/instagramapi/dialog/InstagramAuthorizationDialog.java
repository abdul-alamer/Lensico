package de.gerogerke.android.lensico.instagramapi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import de.gerogerke.android.lensico.ApplicationData;
import de.gerogerke.android.lensico.R;
import de.gerogerke.android.lensico.instagramapi.InstagramAPI;
import de.gerogerke.android.lensico.util.StringUtil;

/**
 * Created by Deutron on 28.03.2016.
 */
public class InstagramAuthorizationDialog extends Dialog {

    private String mUrl;
    private WebView mWebView;
    private DialogSuccessListener dialogSuccessListener;
    private InstagramAPI.OAuthDialogListener mListener;

    public InstagramAuthorizationDialog(Context context, String url, InstagramAPI.OAuthDialogListener listener, DialogSuccessListener dialogSuccessListener) {
        super(context);
        mUrl = url;
        mListener = listener;
        this.dialogSuccessListener = dialogSuccessListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        ViewGroup mSignInView = (ViewGroup) getLayoutInflater().inflate(R.layout.instagram_dialog, null);
        setContentView(mSignInView);
        setCancelable(true);
        webViewSettings(mSignInView, (ProgressBar) mSignInView.findViewById(R.id.instagram_dialog_webview_spinner));
    }

    private void webViewSettings(ViewGroup parent, ProgressBar spinner) {
        mWebView = (WebView) parent.findViewById(R.id.instagram_dialog_webview);
        mWebView.setWebViewClient(new OAuthWebViewClient(spinner));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(mUrl);
    }

    private class OAuthWebViewClient extends WebViewClient {

        ProgressBar mSpinner;

        public OAuthWebViewClient(ProgressBar mSpinner) {
            this.mSpinner = mSpinner;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            StringUtil.log("Redirecting URL " + url);
            if (url.startsWith(ApplicationData.CALLBACK_URL)) {
                String urls[] = url.split("=");
                dialogSuccessListener.onSucceed(urls[1]);
                mSpinner.setVisibility(View.GONE);
                dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            StringUtil.log("Page error: " + description);
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (mListener != null) {
                mListener.onError(description);
            }
            dialogSuccessListener.onFail();
            dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            StringUtil.log("Loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            mSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            StringUtil.log("onPageFinished URL: " + url);
            mSpinner.setVisibility(View.GONE);
        }

    }

    public interface DialogSuccessListener {
        void onSucceed(String token);
        void onFail();
    }

}
