
import 'models/login_model.dart';
import 'native_oauth_ids_platform_interface.dart';

class NativeOauthIds {
  Future<LoginData?> login(String path) {
    return NativeOauthIdsPlatform.instance.login(path);
  }
}
