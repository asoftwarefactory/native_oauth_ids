package app.login.ids.native_oauth_ids
import android.app.Activity
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class Login : Activity() {

    private lateinit var urlInput: String;
    val errorUrl = "about:blank"
    private lateinit var webView:WebView;
    private var bundle: Bundle?=null;
    private lateinit var progressDialog:ProgressDialog;

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = savedInstanceState
        val b = intent.extras
        urlInput = b?.getString("url") ?: errorUrl
        webView= WebView(this.applicationContext)
        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setTitle("Caricamento ...")
        initWebView(urlInput)
        setContentView(webView)
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyWebView()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initWebView(url : String){
        val chromeClient = WebChromeClient()
        webView.webChromeClient.apply { chromeClient }
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                onChangeUrl(webView, url);
                return true
            }
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if(error.errorCode==-6){
                    Log.d("CACHE ERROR : ",error.errorCode.toString())
                  return
                }
                Log.d("WEB VIEW URL ERROR : ",view.url?:"")
                Log.d("LOAD URL ERROR : ",error.errorCode.toString())
                Log.d("ERROR DESCRIPTION : ",error.description.toString())
                errorLogin()
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                onPageStarted()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressDialog?.hide()
            }
        }
        webView?.settings?.databaseEnabled = true
        webView?.settings?.domStorageEnabled = true
        webView.settings.apply {
            javaScriptEnabled = true
            allowContentAccess = true
            allowFileAccess = true
            allowFileAccessFromFileURLs = false
            allowUniversalAccessFromFileURLs = false
        }
        webView.settings.setSupportZoom(true)
        webView.loadUrl(url)
    }

    fun onChangeUrl(view: WebView, urlString: String):Unit{
        Log.d("URL CHANGED",urlString)
        val sessioStateKey = "session_state";
        val codeKey = "code"
        if(urlString.contains(sessioStateKey) && urlString.contains(codeKey)){
            val url : Uri = Uri.parse(urlString)
            val query = "$codeKey=${url.getQueryParameter(codeKey)?:""}&$sessioStateKey=${url.getQueryParameter(sessioStateKey)?:""}"
            sendLoginData(query)
            closeActivity()
            return
        }
        if (urlString.contains("OpenApp")) {
            try {
                val intent = Intent()
                intent.setClassName("it.ipzs.cieid", "it.ipzs.cieid.BaseActivity")
                intent.data = Uri.parse(urlString)
                intent.action = Intent.ACTION_VIEW
                startActivityForResult(intent, 0);
                return
            } catch (a: ActivityNotFoundException) {
                errorLogin()
                return
            }
        }
        view.loadUrl(urlString)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val data: Intent? = data
            val url = data?.getStringExtra("URL")
            if (!TextUtils.isEmpty(url)) {
                if (url != null) {
                    webView.loadUrl(url)
                }else{
                    errorLogin()
                }
            }else{
                errorLogin()
            }
        }else{
            errorLogin()
        }
    }

    private fun sendLoginData(data:String) {
        val intent = Intent("LOGIN_SUCCESS")
        intent.putExtra("login_data", data)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun destroyWebView() {
        webView.removeAllViews()
        webView.destroyDrawingCache()
        webView.pauseTimers()
        webView.clearCache(true)
        webView.clearHistory()
        val webSettings: WebSettings = webView.getSettings()
        webSettings.saveFormData = false
        webSettings.savePassword = false
        webView.destroy();
        clearCookiesWebView();
    }

    private fun clearCookiesWebView(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        } else {
            val cookieSyncMngr = CookieSyncManager.createInstance(this)
            cookieSyncMngr.startSync()
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookie()
            cookieManager.removeSessionCookie()
            cookieSyncMngr.stopSync()
            cookieSyncMngr.sync()
        }
    }

    private fun onPageStarted(){
        progressDialog.show()
    }

    private fun errorLogin(){
        // Toast.makeText(applicationContext, "Error Login", Toast.LENGTH_SHORT).show()
        sendLoginData("ERROR")
        closeActivity()
    }

    private fun closeActivity(){
        finish()
    }

}