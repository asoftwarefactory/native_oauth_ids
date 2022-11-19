import Flutter
import UIKit

public class SwiftNativeOauthIdsPlugin: NSObject, FlutterPlugin, CieIdDelegate {

    var eventSink: FlutterEventSink?

    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "native_oauth_ids", binaryMessenger: registrar.messenger())
        let eventChannel = FlutterEventChannel(name: "events_channel", binaryMessenger: registrar.messenger())
        let instance = SwiftNativeOauthIdsPlugin()
        eventChannel.setStreamHandler(instance)
        registrar.addApplicationDelegate(instance)
        registrar.addMethodCallDelegate(instance, channel: channel)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
            if(call.method == "startLogin"){

                if let args = call.arguments as? [String] {
                    if args.count == 1 {
                        NotificationCenter.default.addObserver(self, selector: #selector(self.onNotification(notification:)), name: Notification.Name(IDNotificationManager.NOTIFICATION_CENTER_NAME), object: nil)
                        let path = args[0];
                        let cieIDAuthenticator = CieIDWKWebViewController()
                        cieIDAuthenticator.modalPresentationStyle = .fullScreen
                        cieIDAuthenticator.delegate = self
                        let viewController: UIViewController = (UIApplication.shared.delegate?.window??.rootViewController)!;
                        cieIDAuthenticator.path = path;
                        viewController.present(cieIDAuthenticator, animated: true, completion: nil);
                        result("OK")
                    } else {
                        result(FlutterError.init(code: "BAD_ARGS",
                                                message: "Wrong argument types",
                                                details: nil))
                    }
                }

            }

            if(call.method == "getPlatformVersion"){

                result("iOS " + UIDevice.current.systemVersion)

            }

            result(FlutterError.init(code: "BAD_METHOD_NAME",
                                                message: "Wrong argument types",
                                                details: nil))

        }

    @objc func onNotification(notification: Notification){

            if let dict = notification.userInfo as Dictionary? {

                if(dict["code"] != nil && dict["session_state"] != nil){

                    let code = (dict["code"]!) as! String;

                    let session_state = (dict["session_state"]!) as! String;

                    self.eventSink?("code=\(code)&session_state=\(session_state)")

                }

            } else {

            }

        }

        func  CieIDAuthenticationClosedWithSuccess() {

        }

        func  CieIDAuthenticationCanceled() {

        }

        func  CieIDAuthenticationClosedWithError(errorMessage: String) {

        }

}


extension SwiftNativeOauthIdsPlugin: FlutterStreamHandler {

    public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        self.eventSink = events
        return nil
    }

    public func onCancel(withArguments arguments: Any?) -> FlutterError? {
        self.eventSink = nil
        return nil
    }
}