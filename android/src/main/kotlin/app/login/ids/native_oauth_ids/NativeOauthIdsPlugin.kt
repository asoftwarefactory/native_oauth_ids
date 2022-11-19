package app.login.ids.native_oauth_ids
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

class NativeOauthIdsPlugin: FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler, ActivityAware {
  private lateinit var channel : MethodChannel
  private lateinit var eventChannel : EventChannel
  private lateinit var activity: Activity
  private lateinit var context: Context
  private var eventSink: EventChannel.EventSink? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "native_oauth_ids")
    channel.setMethodCallHandler(this)
    eventChannel = EventChannel(flutterPluginBinding.binaryMessenger,"events_channel")
    eventChannel.setStreamHandler(this)
  }

  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    if(call.method == "startLogin"){
      var url = call.argument<String>("url");
      if(url==null){
        result.error("Argument Error",null,null)
      }
      LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, IntentFilter("LOGIN_SUCCESS"));
      result.success("OK")
      startLogin(url?:"")
    }else{
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    eventSink = events
  }

  override fun onCancel(arguments: Any?) {
    eventSink = null
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    context = activity.baseContext
  }

  override fun onDetachedFromActivityForConfigChanges() {}

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity
    context = activity.baseContext
  }

  override fun onDetachedFromActivity() {}

  private fun startLogin(url:String){
    val intent= Intent(context,Login::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val b = Bundle()
    b.putString("url", url);
    intent.putExtras(b);
    startActivity(activity, intent, null);
  }

  private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val loginData = intent?.getStringExtra("login_data")
        if (!TextUtils.isEmpty(loginData)) {
          if (loginData != null) {
            eventSink?.success(loginData)
            unregisterReceiver()
          } else{
            eventSink?.success("")
            unregisterReceiver()
          }
        }
    }
  }

  fun unregisterReceiver(){
    LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
  }

}
