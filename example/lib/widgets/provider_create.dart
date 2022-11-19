/*import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class CreateNotifierProvider<T extends ChangeNotifier?>
    extends StatelessWidget {
  final T instance;
  final Widget Function(BuildContext, T) builder;
  const CreateNotifierProvider({
    super.key,
    required this.instance,
    required this.builder,
  });

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider<T>(
      create: (ctx) => instance,
      lazy: false,
      builder: (ctx, _) => builder.call(
        ctx,
        Provider.of<T>(ctx, listen: false),
      ),
    );
  }
}

class ListenProvider<T> extends StatelessWidget {
  final Widget Function(BuildContext, T, Widget?) builder;
  const ListenProvider({super.key, required this.builder});

  @override
  Widget build(BuildContext context) {
    return Consumer<T>(
      builder: builder,
    );
  }
}

class CreateProvider<T> extends StatelessWidget {
  final T instance;
  final Widget Function(BuildContext, T) builder;
  const CreateProvider({
    super.key,
    required this.instance,
    required this.builder,
  });

  @override
  Widget build(BuildContext context) {
    return Provider<T>(
      create: (ctx) => instance,
      lazy: false,
      builder: (ctx, _) => builder.call(
        ctx,
        Provider.of<T>(ctx, listen: false),
      ),
    );
  }
} */
