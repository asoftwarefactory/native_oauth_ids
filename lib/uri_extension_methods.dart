import 'models/login_model.dart';

extension ExtUri on Uri {
  static const codeKey = "code";
  static const sessionStateKey = "session_state";

  LoginData? getLoginData() {
    final state = checkCodeAndSessionState();
    if (state) {
      return LoginData(
        code: queryParameters[codeKey]!,
        sessionState: queryParameters[sessionStateKey]!,
      );
    }
    return null;
  }

  bool checkRedirectUri() {
    return queryParameters["redirect_uri"] != null;
  }

  bool checkCodeAndSessionState() {
    const codeKey = "code";
    const sessionStateKey = "session_state";

    if (queryParameters[codeKey] != null && queryParameters[sessionStateKey] != null) {
      return true;
    }
    return false;
  }

  String? getRedirectUri() {
    return queryParameters["redirect_uri"];
  }
}
