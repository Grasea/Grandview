/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.fancyview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import grandroid.action.Action;
import grandroid.action.AlertAction;
import java.io.File;

/**
 *
 * @author Bingue
 */
public class AdvanceWebView extends WebView {

    protected Context context;
    //private final String url;
    //private WebView webview;
    protected final String digits = "0123456789ABCDEF";
    protected boolean showDialog;

    public AdvanceWebView(Context context) {
        super(context);
        init(context, false, null);
    }

    public AdvanceWebView(Context context, String url) {
        this(context, url, false, null);
    }

    public AdvanceWebView(Context context, String url, boolean simulateUserAgent) {
        this(context, url, simulateUserAgent, null);
    }

    public AdvanceWebView(Context context, String url, boolean simulateUserAgent, WebViewClient client) {
        super(context);
        //this.ctx = context;
        //this.url = url;
        init(context, simulateUserAgent, client);
        //this.loadData("<html><body><center>Loading...</center></body></html>", "text/html", "utf-8");
        CookieManager.getInstance().setAcceptCookie(true);
        loadUrl(url);
    }

    protected void init(Context context, boolean simulateUserAgent, WebViewClient client) {
        this.context = context;
        showDialog = true;
        WebSettings webSettings = this.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        //webSettings.setPluginsEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);
        try {
            webSettings.setGeolocationEnabled(true);
            webSettings.setGeolocationDatabasePath(context.getFilesDir().getPath());
        } catch (Exception ex) {
        }
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        if (simulateUserAgent) {
            //Log.d("TaiwanNext", "Old UserAgent=" + webSettings.getUserAgentString());
            this.simulateUserAgentAsOldAndroid();
            //Log.d("TaiwanNext", "New UserAgent=" + webSettings.getUserAgentString());
        }
        //Log.d("TaiwanNext", "advance:" + url);
        if (client == null) {
            setWebViewClient(new BasicWebViewClient(true));
        } else {
            setWebViewClient(client);
        }
        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                new AlertAction(view.getContext()).setData(null, message, new Action("確定"), null).execute();
                result.confirm();//因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                new AlertAction(view.getContext()).setData(null, message, new Action("確定") {
                    @Override
                    public boolean execute() {
                        result.confirm();
                        return true;
                    }
                }, new Action("取消") {
                    @Override
                    public boolean execute() {
                        result.cancel();
                        return true;
                    }
                }).execute();
                return true;
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                AdvanceWebView.this.onReceivedTitle(AdvanceWebView.this, title);
            }

            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                if (AdvanceWebView.this.context instanceof Activity) {
                    ((Activity) AdvanceWebView.this.context).setProgress(progress * 1000);
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                    GeolocationPermissions.Callback callback) {
                // Always grant permission since the app itself requires location
                // permission and the user has therefore already granted it
                callback.invoke(origin, true, false);
            }
        });
    }

    public void silence() {
        showDialog = false;
    }

    public void loadImage(String url, boolean fitView) {
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setUseWideViewPort(true);
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        setScrollbarFadingEnabled(true);
        if (fitView) {
            loadData("<html><head></head><body style=\"margin: 0px 0px 0px 0px;padding:0\"><img id=\"pic\" src=\"" + url + "\" width=\"100%\" height=\"100%\"/></body></html>", "text/html", "UTF-8");
        } else {
            loadData("<html><head></head><body style=\"margin: 0px 0px 0px 0px;padding:0\"><img id=\"pic\" src=\"" + url + "\"/></body></html>", "text/html", "UTF-8");
        }
    }

    @Override
    public void loadData(String data, String mimeType, String encoding) {
        super.loadData(encode(data), mimeType, encoding);
    }

    public String encode(String s) {
        // Guess a bit bigger for encoded form
        StringBuilder buf = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')
                    || (ch >= '0' && ch <= '9') || ".-*_".indexOf(ch) > -1) { //$NON-NLS-1$
                buf.append(ch);
            } else {
                byte[] bytes = new String(new char[]{ch}).getBytes();
                for (int j = 0; j < bytes.length; j++) {
                    buf.append('%');
                    buf.append(digits.charAt((bytes[j] & 0xf0) >> 4));
                    buf.append(digits.charAt(bytes[j] & 0xf));
                }
            }
        }
        return buf.toString();
    }

    public AdvanceWebView enableZoom() {
        this.getSettings().setBuiltInZoomControls(true);
        return this;
    }

    public AdvanceWebView enableWideViewPort() {
        this.getSettings().setUseWideViewPort(true);
        this.getSettings().setLoadWithOverviewMode(true);
        setInitialScale(1);
        return this;
    }

    public AdvanceWebView enableCache() {
        return enableCache(WebSettings.LOAD_DEFAULT);
    }

    public AdvanceWebView enableCache(int mode) {
        // 开启 DOM storage API 功能 
        getSettings().setDomStorageEnabled(true);
        //开启 database storage API 功能 
        getSettings().setDatabaseEnabled(true);
        File cacheDir = new File(context.getExternalCacheDir(), "webview_cache");
        cacheDir.mkdirs();
        String cacheDirPath = cacheDir.getAbsolutePath();
        //设置数据库缓存路径 
        getSettings().setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录 
        getSettings().setAppCachePath(cacheDirPath);

        this.getSettings().setAppCacheEnabled(true);
        this.getSettings().setCacheMode(mode);
        return this;
    }

    public void simulateUserAgentAsOldAndroid() {
        String userAgent = this.getSettings().getUserAgentString();
        int indexStart = userAgent.indexOf("Android ");
        if (indexStart > 0) {
            int indexEnd = indexStart + 8;
            while (userAgent.substring(indexEnd, indexEnd + 1).matches("[0-9.]")) {
                indexEnd++;
            }
            userAgent = userAgent.substring(0, indexStart) + "Android 2.1" + userAgent.substring(indexEnd);
            Log.d("grandroid", "AdvanceWebView simulate UserAgent: " + userAgent);
        }
        this.getSettings().setUserAgentString(userAgent);
    }

    public void simulateUserAgentAsIPhone() {
        this.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543a Safari/419.3");
    }

    public boolean shouldOverrideUrlLoading(String url) {
        Log.d("grnadroid", "shouldOverrideUrlLoading " + url);
        if (url.startsWith("vnd.youtube:")) {
            Log.d("grandroid", "detected youtube movie, start new intent to play it..." + url);
            int n = url.indexOf("?");
            String uri;
            if (n > 0) {
                uri = String.format("http://www.youtube.com/v/%s", url.substring("vnd.youtube:".length(), n));
            } else {
                uri = String.format("http://www.youtube.com/v/%s", url.substring("vnd.youtube:".length()));
            }
            Log.d("grandroid", "try to play " + uri);
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
            return true;
        }
        if (url.endsWith(".3gp")) {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            getContext().startActivity(intent);
            return true;
        }
        if (url.startsWith("market://")) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(i);
            return true;
        }
        if (url.contains("//play.google.com/store/apps")) {
            String[] s = url.substring(url.indexOf("?") + 1).split("&");
            for (int i = 0; i < s.length; i++) {
                if (s[i].startsWith("id=")) {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?" + s[i])));
                    return true;
                }
            }
        }
        if (url.startsWith("http://line.naver.jp/") || !url.toLowerCase().startsWith("http")) {
            try {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            } catch (Exception ex) {
                Log.d("grandroid", "AdvanceWebView parse uri fail.." + ex.toString());
            }
            //return super.shouldOverrideUrlLoading(view, url);
        } else {
            loadUrl(url);
            return true;
        }
        return false;
    }

    public void onReceivedTitle(AdvanceWebView view, String title) {
    }

    public void onLoadResource(WebView view, String url) {
    }

    public boolean handleBackKey(Activity activity, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (this.canGoBack() == true) {
                this.goBack();
            } else {
                activity.finish();
            }
            return true;
        }
        return false;
    }

}
