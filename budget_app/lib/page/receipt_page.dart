import 'package:budget_app/cache/receipt_cache.dart';
import 'package:budget_app/component/component_load_state.dart';
import 'package:budget_app/extension/date_extension.dart';
import 'package:budget_app/model/receipt.dart';
import 'package:budget_app/page/receipt_detail_page.dart';
import 'package:budget_app/page/settings_page.dart';
import 'package:budget_app/provider/service_provider.dart';
import 'package:flutter/material.dart';

class ReceiptPage extends StatefulWidget {
  const ReceiptPage({super.key});

  @override
  State<StatefulWidget> createState() => _ReceiptPageState();
}

class _ReceiptPageState extends State<ReceiptPage> {
  ComponentLoadState _loadState = .loading;

  List<Receipt> _receipts = [];

  @override
  void initState() {
    super.initState();
    _load();
  }

  void _load() async {
    setState(() {
      _loadState = .loading;
    });
    final receiptService = ServiceProvider.receiptServiceOf(context);
    _receipts = await receiptService.getAll();
    setState(() {
      _loadState = .loaded;
    });
  }

  void _openDetailsPage(Receipt? rec) async {
    final n = Navigator.of(context);
    await n.push(
      MaterialPageRoute<Receipt>(builder: (ctx) => ReceiptDetailPage(rec)),
    );
    _load();
  }

  Future<void> handleRefresh() async {
    showAboutDialog(context: context);
  }

  List<Widget> _buildReceiptList() {
    final t = Theme.of(context);

    return _receipts.map((rec) {
      return GestureDetector(
        onTap: () {
          _openDetailsPage(rec);
        },
        child: Card(
          elevation: 4,
          color: t.colorScheme.primary,
          child: Padding(
            padding: EdgeInsetsGeometry.symmetric(vertical: 32),
            child: Row(
              mainAxisAlignment: .spaceAround,
              children: [
                Text(
                  rec.date.formatDate(),
                  style: t.textTheme.bodyMedium?.copyWith(
                    color: t.colorScheme.onPrimary,
                  ),
                ),
                Text(
                  "${rec.total}",
                  style: t.textTheme.bodyMedium?.copyWith(
                    color: t.colorScheme.onPrimary,
                  ),
                ),
              ],
            ),
          ),
        ),
      );
    }).toList();
  }

  @override
  Widget build(BuildContext context) {
    final t = Theme.of(context);
    Widget body;

    switch (_loadState) {
      case .loading:
        body = Center(child: CircularProgressIndicator());
        break;
      case .loaded:
        body = RefreshIndicator(
          onRefresh: handleRefresh,
          child: ListView(children: _buildReceiptList()),
        );
        break;
    }

    return Scaffold(
      appBar: AppBar(
        title: Text(
          "Receipts",
          style: t.textTheme.headlineLarge?.copyWith(
            color: t.colorScheme.surface,
          ),
        ),
        actions: [
          IconButton(
              onPressed: (){
                Navigator.of(context).push(
                    MaterialPageRoute(builder: (context) => SettingsPage())
                );
              },
              icon: Icon(Icons.settings, color: t.colorScheme.surface)
          )
        ],
        backgroundColor: t.colorScheme.onSurface,
      ),
      body: SafeArea(child: body),
      bottomNavigationBar: SafeArea(
        child: ElevatedButton(
          onPressed: () => _openDetailsPage(null),
          child: Text("CREATE NEW"),
        ),
      ),
    );
  }
}
