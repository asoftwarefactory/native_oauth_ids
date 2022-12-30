package nativeoauthids;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class Login extends Activity {

  private String urlInput;
  private final String errorUrl = "about:blank";
  private WebView webView;
  private Bundle bundle;
  private ProgressDialog progressDialog;

  @RequiresApi(Build.VERSION_CODES.O)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bundle = savedInstanceState;
    Bundle b = getIntent().getExtras();
    urlInput = b != null ? b.getString("url") : errorUrl;
    webView = new WebView(this);
    initLoadingDialog();
    setContentView(webView);
    initWebView(urlInput);
  }

  @Override
  protected void onDestroy() {
    // sendLoginData("ERROR");
    super.onDestroy();
  }

  @Override
  public void onBackPressed() {
    sendLoginData("ERROR");
    super.onBackPressed();
  }

  private void initWebView(String url) {
    WebChromeClient chromeClient = new WebChromeClient();
    webView.setWebChromeClient(chromeClient);
    webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    webView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        onChangeUrl(webView, url);
        return true;
      }

      @RequiresApi(Build.VERSION_CODES.M)
      @Override
      public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if (error.getErrorCode() == -6) {
          Log.d("CACHE ERROR : ", String.valueOf(error.getErrorCode()));

          disposeLoadingDialog();
          return;
        }
        Log.d("WEB VIEW URL ERROR : ", view.getUrl() != null ? view.getUrl() : "");
        Log.d("LOAD URL ERROR : ", String.valueOf(error.getErrorCode()));
        Log.d("ERROR DESCRIPTION : ", error.getDescription().toString());
        sendLoginData("ERROR");
      }

      @Override
      public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        showLoadingDialog();
      }

      @Override
      public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        disposeLoadingDialog();
      }

    });

    webView.getSettings().setDatabaseEnabled(true);
    webView.getSettings().setDomStorageEnabled(true);
    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setAllowContentAccess(true);
    settings.setAllowFileAccess(true);
    settings.setAllowFileAccessFromFileURLs(false);
    settings.setAllowUniversalAccessFromFileURLs(false);
    webView.getSettings().setSupportZoom(true);
    webView.loadUrl(url);
  }

  private void onChangeUrl(WebView view, String urlString) {
    Log.d("URL CHANGED", urlString);
    String sessioStateKey = "session_state";
    String codeKey = "code";
    if (urlString.contains(sessioStateKey) && urlString.contains(codeKey)) {
      Uri url = Uri.parse(urlString);
      String query = codeKey + "=" + (url.getQueryParameter(codeKey) != null ? url.getQueryParameter(codeKey) : "")
          + "&" + sessioStateKey + "="
          + (url.getQueryParameter(sessioStateKey) != null ? url.getQueryParameter(sessioStateKey) : "");
      sendLoginData(query);
      termiateLogin();
      return;
    }
    if (urlString.contains("OpenApp")) {
      Log.d("urlString","LOGIN CIE");
      try {
        Intent intent = new Intent();
        if(urlString.toLowerCase().contains("preproduzione.idserver.servizicie")){
          Log.d("urlString","LOGIN CIE PREPRODUZIONE");
          intent.setClassName("it.ipzs.cieid.collaudo", "it.ipzs.cieid.BaseActivity");
        }else{
          intent.setClassName("it.ipzs.cieid", "it.ipzs.cieid.BaseActivity");
        }
        intent.setData(Uri.parse(urlString));
        intent.setAction(Intent.ACTION_VIEW);
        startActivityForResult(intent, 0);
        return;
      } catch (ActivityNotFoundException a) {
        sendLoginData("ERROR");
        return;
      }
    }
    view.loadUrl(urlString);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK) {
      String url = data.getStringExtra("URL");
      if (!TextUtils.isEmpty(url)) {
        if (url != null) {
          webView.loadUrl(url);
        } else {
          sendLoginData("ERROR");
        }
      } else {
        sendLoginData("ERROR");
      }
    } else {
      sendLoginData("ERROR");
    }
  }

  private void sendLoginData(String data) {
    Intent intent = new Intent("LOGIN_SUCCESS");
    intent.putExtra("login_data", data);
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    termiateLogin();
  }

  private void destroyWebView() {
    webView.removeAllViews();
    webView.destroyDrawingCache();
    webView.pauseTimers();
    webView.clearCache(true);
    webView.clearHistory();
    // WebSettings webSettings = webView.getSettings();
    webView.destroy();
  }

  private void destroyAndClearCookies(){
    destroyWebView();
    clearCookiesWebView();
  }

  private void clearCookiesWebView() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
      CookieManager.getInstance().removeAllCookies(null);
      CookieManager.getInstance().flush();
    } else {
      CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
      cookieSyncMngr.startSync();
      CookieManager cookieManager = CookieManager.getInstance();
      cookieManager.removeAllCookie();
      cookieManager.removeSessionCookie();
      cookieSyncMngr.stopSync();
      cookieSyncMngr.sync();
    }
  }

  private void finishActivity(){
    finish();
  }

  private void initLoadingDialog(){
    progressDialog = new ProgressDialog(this);
    progressDialog.setCanceledOnTouchOutside(false);
    progressDialog.setCancelable(true);
    progressDialog.setTitle("Caricamento ...");
    progressDialog.create();
  }

  private void showLoadingDialog(){
    progressDialog.show();
  }

  private void closeLoadingDialog(){
    progressDialog.hide();
  }

  private void disposeLoadingDialog(){
    progressDialog.dismiss();
  }

  private void termiateLogin(){
    closeLoadingDialog();
    destroyAndClearCookies();
    finishActivity();
  }

}
