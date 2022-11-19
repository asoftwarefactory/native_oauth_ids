import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_web_auth/flutter_web_auth.dart';
import 'package:native_oauth_ids/plaftorms.dart';
import 'package:native_oauth_ids/uri_extension_methods.dart';
import 'models/login_model.dart';
import 'native_oauth_ids_platform_interface.dart';

class MethodChannelNativeOauthIds extends NativeOauthIdsPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('native_oauth_ids');

  @visibleForTesting
  final streamChannel = const EventChannel('events_channel');

  Future<LoginData?> _loginWeb(Uri uri, {String? redirectUri}) async {
    if (redirectUri == null) {
      throw Exception("RedirectUri REQUIRED");
    }
    final login = await FlutterWebAuth.authenticate(
      url: uri.path,
      callbackUrlScheme: redirectUri,
      preferEphemeral: false,
    );
    final loginUri = Uri(path: login);
    return loginUri.getLoginData();
  }

  @override
  Future<LoginData?> login(String path, {String? redirectUri}) async {
    path = path.trim();
    final uri = Uri(path: path);
    if (isWeb) {
      return await _loginWeb(uri, redirectUri: redirectUri);
    }
    final login = await methodChannel.invokeMethod<String?>(
        'startLogin', isIOS ? [path] : {"url": path});
    if (login == "OK") {
      final queryData = (await _eventStream().first);
      final loginUri = Uri(query: queryData);
      return loginUri.getLoginData();
    }
    return null;
  }

  Stream<String> _eventStream() {
    return (streamChannel.receiveBroadcastStream().map<String>((event) {
      return (event ?? '').toString();
    })).asBroadcastStream();
  }
}
