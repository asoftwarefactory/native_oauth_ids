// In order to *not* need this ignore, consider extracting the "web" version
// of your plugin as a separate package, instead of inlining it in the same
// package as the core of your plugin.

// ignore: avoid_web_libraries_in_flutter
// import 'dart:html' as html show window;
import 'package:flutter_web_auth/flutter_web_auth.dart';
// ignore: depend_on_referenced_packages
import 'package:flutter_web_plugins/flutter_web_plugins.dart';
import 'package:native_oauth_ids/utils/uri_extension_methods.dart';
import 'models/login_model.dart';
import 'native_oauth_ids_platform_interface.dart';

class NativeOauthIdsWeb extends NativeOauthIdsPlatform {
  NativeOauthIdsWeb();

  static void registerWith(Registrar registrar) {
    NativeOauthIdsPlatform.instance = NativeOauthIdsWeb();
  }

  @override
  Future<LoginData?> login(String path, {String? redirectUri}) async {
    final uri = Uri(path: path);
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
}
