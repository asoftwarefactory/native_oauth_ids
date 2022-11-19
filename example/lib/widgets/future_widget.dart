import 'package:flutter/material.dart';
import 'resolve_snapshot/resolve_snapshot.dart';

class FutureWidget<T> extends StatelessWidget {
  final Future<T> future;
  final Widget Function(BuildContext, T) builder;
  final Widget Function(BuildContext, dynamic)? onError;
  final Widget Function(BuildContext)? onLoading;
  const FutureWidget({
    super.key,
    required this.future,
    required this.builder,
    this.onError,
    this.onLoading,
  });

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<T>(
      future: future,
      builder: (ctx, snapshot) {
        return ResolveSnapshot<T>(
          snapshot: snapshot,
          onData: (ctx, data) => builder.call(ctx, data),
          onError: onError?.call(ctx, null),
          loading: onLoading?.call(ctx),
        );
      },
    );
  }
}
