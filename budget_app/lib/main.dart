import 'dart:async';

import 'package:budget_app/cache/receipt_cache.dart';
import 'package:budget_app/cache/receipt_line_cache.dart';
import 'package:budget_app/component/component_load_state.dart';
import 'package:budget_app/message_bus/message_bus.dart';
import 'package:budget_app/page/receipt_page.dart';
import 'package:budget_app/provider/initializer.dart';
import 'package:budget_app/provider/service_provider.dart';
import 'package:budget_app/repository/receipt_line_repository.dart';
import 'package:budget_app/repository/receipt_repository.dart';
import 'package:budget_app/service/domain_service.dart';
import 'package:budget_app/service/receipt_line_service.dart';
import 'package:budget_app/service/receipt_service.dart';
import 'package:flutter/material.dart';

void main() {
  final receiptCache = ReceiptCache();
  final receiptRepo = ReceiptRepository();

  final receiptLineCache = ReceiptLineCache();
  final receiptLineRepo = ReceiptLineRepository();

  final initializer = Initializer([
    receiptRepo,
    receiptLineRepo
  ]);

  final messageBus = MessageBus();

  final caches = <String, DomainService>{
    "receipt": ReceiptService(receiptCache, receiptRepo),
    "receiptLine": ReceiptLineService(receiptLineCache, receiptLineRepo),
  };
  runApp(ServiceProvider(caches, messageBus, child: BudgetApp(initializer)));
}

class BudgetApp extends StatefulWidget {
  final Initializer _initializer;

  const BudgetApp(this._initializer, {super.key});

  @override
  State<StatefulWidget> createState() => _BudgetAppState();
}

class _BudgetAppState extends State<BudgetApp> {
  ComponentLoadState _loadState = .loading;

  ThemeMode _themeMode = ThemeMode.system;

  Color _seedColor = Colors.green;

  late StreamSubscription<MapEntry<String, Object?>> _sub;

  @override
  void initState() {
    super.initState();
    _load();
  }

  @override
  void dispose() {
    _sub.cancel();
    super.dispose();
  }

  void _load() async {
    setState(() {
      _loadState = .loading;
    });

    final messageBus = ServiceProvider.messageBusOf(context);
    _sub = messageBus.sub((entry) {
      if(entry.key == "themeMode") {
        setState(() {
          _themeMode = entry.value as ThemeMode;
        });
      } else if (entry.key == "color") {
        setState(() {
          _seedColor = entry.value as Color;
        });
      }
    });
    await widget._initializer.init();

    setState(() {
      _loadState = .loaded;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          brightness: Brightness.light,
          seedColor: _seedColor,
        ),
      ),
      darkTheme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          brightness: Brightness.dark,
          seedColor: _seedColor,
        ),
      ),
      themeMode: _themeMode,
      home: _loadState == .loading
          ? const Material(child: Center(child: CircularProgressIndicator()))
          : const ReceiptPage(),
    );
  }
}
