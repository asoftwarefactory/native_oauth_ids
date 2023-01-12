import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:native_oauth_ids/utils/uri_extension_methods.dart';
import 'models/login_model.dart';
import 'native_oauth_ids_platform_interface.dart';
import 'utils/plaftorms.dart';

class MethodChannelNativeOauthIds extends NativeOauthIdsPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('native_oauth_ids');

  @visibleForTesting
  final streamChannel = const EventChannel('events_channel');

  @override
  Future<LoginData?> login(String path, {String? redirectUri}) async {
    path = path.trim();
    final login = await methodChannel.invokeMethod<String?>(
      'startLogin',
      isIOS ? [path] : {"url": path},
    );
    if (login == "OK") {
      final queryData = (await _eventStream().first);
      final loginUri = Uri(query: queryData);
      return loginUri.getLoginData();
    } else {
      return null;
    }
  }

  Stream<String> _eventStream() {
    return (streamChannel.receiveBroadcastStream().map<String>((event) {
      return (event ?? '').toString();
    })).asBroadcastStream();
  }
}
