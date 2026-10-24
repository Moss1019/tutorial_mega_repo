
import 'dart:async';

import 'package:budget_app/message_bus/message_bus.dart';
import 'package:budget_app/service/domain_service.dart';
import 'package:budget_app/service/receipt_line_service.dart';
import 'package:budget_app/service/receipt_service.dart';
import 'package:flutter/cupertino.dart';

class ServiceProvider extends InheritedWidget {
  final Map<String, DomainService> _services;

  final MessageBus _messageBus;

  const ServiceProvider(this._services, this._messageBus, {required super.child, super.key});

  @override
  bool updateShouldNotify(covariant InheritedWidget oldWidget) {
    return false;
  }

  static MessageBus messageBusOf(BuildContext context) {
    final provider = context.getInheritedWidgetOfExactType<ServiceProvider>();
    return provider!._messageBus;
  }

  static ReceiptService receiptServiceOf(BuildContext context) {
    final provider = context.getInheritedWidgetOfExactType<ServiceProvider>();
    return provider!._services["receipt"]! as ReceiptService;
  }

  static ReceiptLineService receiptLineServiceOf(BuildContext context) {
    final provider = context.getInheritedWidgetOfExactType<ServiceProvider>();
    return provider!._services["receiptLine"]! as ReceiptLineService;
  }
}
