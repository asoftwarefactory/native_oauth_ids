package app.login.ids.native_oauth_ids
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.webkit.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class Login : Activity() {
    private lateinit var urlInput: String;
    val errorUrl = "about:blank"
    private lateinit var webView:WebView;
    private var bundle: Bundle?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = savedInstanceState
        val b = intent.extras
        urlInput = b?.getString("url") ?: errorUrl
        webView= WebView(this.applicationContext)
        initWebView(urlInput)
        setContentView(webView)
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyWebView()
    }

    fun initWebView(url : String){
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ONLY
        webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                return onChangeUrl(webView, url);
            }
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                // Toast.makeText(activity, "Got Error! $error", Toast.LENGTH_SHORT).show()
                view.loadUrl(errorUrl)
                return
            }
        }
        webView?.settings?.databaseEnabled = true
        webView?.settings?.domStorageEnabled = true
        webView.settings.apply {
            javaScriptEnabled = true
            allowContentAccess = false
            allowFileAccess = false
            allowFileAccessFromFileURLs = false
            allowUniversalAccessFromFileURLs = false
        }
        webView.settings.setSupportZoom(true) //TODO :
        webView.loadUrl(url)
    }

    fun onChangeUrl(view: WebView, urlString: String):Boolean{
        Log.d("URL CHANGE : ", urlString)
        val sessioStateKey = "session_state";
        val codeKey = "code"
        if(urlString.contains(sessioStateKey) && urlString.contains(codeKey)){
            val url : Uri = Uri.parse(urlString)
            val query = "$codeKey=${url.getQueryParameter(codeKey)?:""}&$sessioStateKey=${url.getQueryParameter(sessioStateKey)?:""}"
            sendLoginData(query)
            closeActivity()
        }else if (urlString.contains("OpenApp")) {
            try {
                val intent = Intent()
                intent.setClassName("it.ipzs.cieid", "it.ipzs.cieid.BaseActivity")
                intent.data = Uri.parse(urlString)
                intent.action = Intent.ACTION_VIEW
                startActivityForResult(intent,0);
            } catch (a : ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=it.ipzs.cieid")
                    )
                )
            }
        }else{
            view.loadUrl(urlString)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val data: Intent? = data
            val url = data?.getStringExtra("URL")
            if (!TextUtils.isEmpty(url)) {
                if (url != null) {
                    webView.loadUrl(url)
                }
            }
        }
    }

    private fun sendLoginData(data:String) {
        val intent = Intent("LOGIN_SUCCESS")
        intent.putExtra("login_data", data)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun destroyWebView() {
        webView.removeAllViews()
        webView.clearHistory()
        webView.clearCache(true)
        webView.loadUrl("about:blank")
        webView.onPause()
        webView.removeAllViews()
        webView.destroyDrawingCache()
        webView.pauseTimers()
        webView.clearCache(true)
        webView.clearHistory()
        val webSettings: WebSettings = webView.getSettings()
        webSettings.saveFormData = false
        webSettings.savePassword = false
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

    private fun closeActivity(){
        finish()
    }
}