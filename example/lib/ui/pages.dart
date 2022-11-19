import 'dart:developer';
import 'package:flutter/material.dart';
import 'package:native_oauth_ids/native_oauth_ids.dart';

class Home extends StatelessWidget {
  const Home({super.key});

  @override
  Widget build(BuildContext context) {
    String loginData = "";
    return StatefulBuilder(
      builder: (ctx, set) => Scaffold(
        //appBar: AppBar(),
        body: SizedBox.expand(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              ElevatedButton(
                child: const Text("LOGIN"),
                onPressed: () async {
                  final packageMultiplatformsNativePlugin = NativeOauthIds();
                  const smartpaUrl = "https://login.asfweb.it/fbce008c-b9ac-4aa3-928b-9fcaea28fa44/connect/authorize?client_id=portalecittadino&redirect_uri=com.example.packageMultiplatformsNativeExample:/oauthredirect&response_type=code&scope=asfappusers+openid+profile+offline_access+asfappcore+asfappprofile&code_challenge_method=S256&code_challenge=cntRazl9JTJPZsuSAdrEXoQ6Pe94YvgDh5xmRcMJyLg&suppressed_prompt=login&prompt=login&authorityId=73";
                  final str = await packageMultiplatformsNativePlugin
                      .login(smartpaUrl)
                      .then((e) {
                    log(e?.code ?? "null");
                    return e;
                  });
                  set(() => loginData = str?.code ?? '');
                },
              ),
              const SizedBox(height: 50),
              Text(loginData),
            ],
          ),
        ),
      ),
    );
  }
}
