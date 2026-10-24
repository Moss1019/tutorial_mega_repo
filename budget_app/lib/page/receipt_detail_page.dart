import 'package:budget_app/extension/date_extension.dart';
import 'package:budget_app/model/receipt.dart';
import 'package:budget_app/model/receipt_line.dart';
import 'package:budget_app/provider/service_provider.dart';
import 'package:flutter/material.dart';

class ReceiptDetailPage extends StatefulWidget {
  final Receipt? _receipt;

  const ReceiptDetailPage(this._receipt, {super.key});

  @override
  State<StatefulWidget> createState() => _ReceiptDetailPageState();
}

class _LineDetails {
  final String label;

  final double price;

  const _LineDetails(this.label, this.price);
}

class _ReceiptDetailPageState extends State<ReceiptDetailPage> {
  final TextEditingController _labelController = TextEditingController();

  final TextEditingController _priceController = TextEditingController();

  final FocusNode _priceNode = FocusNode();

  List<_LineDetails> _lines = [];

  DateTime _selectedDate = DateTime.now();

  @override
  void initState()  {
    super.initState();
    _load();
  }
  
  void _load() async {
    if (widget._receipt == null) return;
    final receiptLineService = ServiceProvider.receiptLineServiceOf(context);
    final lines = (await receiptLineService.getForReceipt(widget._receipt!.id))
        .map((l) => _LineDetails(l.label, l.price)).toList();
    setState(() {
      _selectedDate = widget._receipt!.date;
      _lines = lines;
    });
  }

  @override
  void dispose() {
    _labelController.dispose();
    _priceController.dispose();
    _priceNode.dispose();
    super.dispose();
  }

  void _selectDate() async {
    final now = DateTime.now();
    final start = now.add(Duration(days: -7));
    final end = now.add(Duration(days: 2));
    final pickedDate = await showDatePicker(
      context: context,
      firstDate: start,
      lastDate: end,
    );
    if (pickedDate != null) {
      setState(() {
        _selectedDate = pickedDate;
      });
    }
  }

  void _createNewLine() {
    final label = _labelController.text;
    if (label.isEmpty) return;
    final price = double.tryParse(_priceController.text);
    if (price == null) return;
    _labelController.clear();
    _priceController.clear();
    setState(() {
      _lines.add(_LineDetails(label, price));
    });
  }

  List<Widget> _buildLines() {
    return _lines.map((l) {
      return Card(
        child: Padding(
          padding: EdgeInsetsGeometry.symmetric(horizontal: 16, vertical: 32),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [Text(l.label), Text("${l.price}")],
          ),
        ),
      );
    }).toList();
  }

  Future<void> _saveReceiptData() async {
    final receiptId = _selectedDate.hashCode;
    double total = 0.0;
    final receiptLineCache = ServiceProvider.receiptLineServiceOf(context);
    for(int i = 0; i < _lines.length; ++i) {
      final l = _lines[i];
      total += l.price;
      final lineId = "$receiptId-$i";
      final receiptLine = ReceiptLine(lineId, receiptId, l.label, l.price);
      await receiptLineCache.insert(receiptLine);
    }
    final receiptCache = ServiceProvider.receiptServiceOf(context);
    final receipt = Receipt(receiptId, _selectedDate, total);
    await receiptCache.insert(receipt);
  }

  @override
  Widget build(BuildContext context) {
    final t = Theme.of(context);

    return PopScope(
      canPop: false,
      onPopInvokedWithResult: (didPop, val) async {
        if(didPop) return;
        await _saveReceiptData();
        Navigator.of(context).pop();
      },
      child: Scaffold(
        appBar: AppBar(
          title: Text(
            "Receipt details",
            style: t.textTheme.headlineLarge?.copyWith(
              color: t.colorScheme.surface,
            ),
          ),
          backgroundColor: t.colorScheme.onSurface,
          iconTheme: IconThemeData(color: t.colorScheme.surface),
        ),
        body: SafeArea(
          child: Padding(
            padding: EdgeInsetsGeometry.symmetric(horizontal: 16),
            child: Column(
              children: [
                if (widget._receipt == null)
                  ElevatedButton(
                    onPressed: _selectDate,
                    child: Text(_selectedDate.formatDate()),
                  )
                else
                  Text(widget._receipt!.date.formatDate()),
                Expanded(child: ListView(children: _buildLines())),
                Row(
                  children: [
                    Expanded(
                      child: TextField(
                        controller: _labelController,
                        textInputAction: TextInputAction.next,
                        onSubmitted: (val) {
                          _priceNode.requestFocus();
                        },
                      ),
                    ),
                    Expanded(
                      child: TextField(
                        focusNode: _priceNode,
                        controller: _priceController,
                        keyboardType: TextInputType.numberWithOptions(
                          decimal: true,
                        ),
                        textInputAction: TextInputAction.done,
                        onSubmitted: (val) {
                          _createNewLine();
                        },
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
