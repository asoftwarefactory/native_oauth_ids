#import "NativeOauthIdsPlugin.h"
#if __has_include(<native_oauth_ids/native_oauth_ids-Swift.h>)
#import <native_oauth_ids/native_oauth_ids-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "native_oauth_ids-Swift.h"
#endif

@implementation NativeOauthIdsPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftNativeOauthIdsPlugin registerWithRegistrar:registrar];
}
@end
