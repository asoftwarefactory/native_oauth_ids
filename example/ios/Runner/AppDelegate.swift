import UIKit
import Flutter

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    GeneratedPluginRegistrant.register(with: self)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }

  override func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey: Any]) -> Bool {
      /// PER CIE LOGIN REDIRECT WITH PARAMS
      // return true
      var urlString : String = String(url.absoluteString)
      if let httpsRange = urlString.range(of: "https://"){

          //Rimozione del prefisso dell'URL SCHEME
          let startPos = urlString.distance(from: urlString.startIndex, to: httpsRange.lowerBound)
          urlString = String(urlString.dropFirst(startPos))

          //Passaggio dell'URL alla WebView
          let response : [String:String] = ["payload": urlString]
          let NOTIFICATION_NAME : String = "RETURN_FROM_CIEID"

          NotificationCenter.default.post(name: 	Notification.Name(NOTIFICATION_NAME), object: nil, userInfo: response)
          return true;

      }

      return true;
  }

}

