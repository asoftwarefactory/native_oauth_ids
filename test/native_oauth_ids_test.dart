import 'package:flutter_test/flutter_test.dart';
import 'package:native_oauth_ids/models/login_model.dart';
import 'package:native_oauth_ids/native_oauth_ids_platform_interface.dart';
import 'package:native_oauth_ids/native_oauth_ids_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockNativeOauthIdsPlatform
    with MockPlatformInterfaceMixin
    implements NativeOauthIdsPlatform {

  @override
  Future<LoginData?> login(String path) {
    throw UnimplementedError();
  }
}

void main() {
  final NativeOauthIdsPlatform initialPlatform = NativeOauthIdsPlatform.instance;

  test('$MethodChannelNativeOauthIds is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelNativeOauthIds>());
  });

  test('getPlatformVersion', () async {
    MockNativeOauthIdsPlatform fakePlatform = MockNativeOauthIdsPlatform();
    NativeOauthIdsPlatform.instance = fakePlatform;
  });
}
