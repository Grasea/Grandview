/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 *
 * @author Rovers
 */
public class BasicWebViewClient extends WebViewClient {

    protected ProgressDialog dialog;
    protected boolean showDialog;

    public BasicWebViewClient(boolean showDialog) {
        this.showDialog = showDialog;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (dialog == null && showDialog) {
            dialog = ProgressDialog.show(view.getContext(), "", "Loading...", true, true, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // do something
                }
            });
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        CookieSyncManager.getInstance().sync();
        if (dialog != null) {
            try{
            dialog.dismiss();
            }catch(Exception ex){
            }
            dialog = null;
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return ((AdvanceWebView) view).shouldOverrideUrlLoading(url);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        //handler.cancel(); //默认的处理方式，WebView变成空白页
        handler.proceed();
        // handleMessage(Message msg); //其他处理
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        ((AdvanceWebView) view).onLoadResource(view, url);
    }

}
