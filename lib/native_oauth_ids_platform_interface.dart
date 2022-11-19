import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'models/login_model.dart';
import 'native_oauth_ids_method_channel.dart';

abstract class NativeOauthIdsPlatform extends PlatformInterface {
  /// Constructs a NativeOauthIdsPlatform.
  NativeOauthIdsPlatform() : super(token: _token);

  static final Object _token = Object();

  static NativeOauthIdsPlatform _instance = MethodChannelNativeOauthIds();

  /// The default instance of [NativeOauthIdsPlatform] to use.
  ///
  /// Defaults to [MethodChannelNativeOauthIds].
  static NativeOauthIdsPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [NativeOauthIdsPlatform] when
  /// they register themselves.
  static set instance(NativeOauthIdsPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<LoginData?> login(String path) {
    throw UnimplementedError('login() has not been implemented.');
  }
}
