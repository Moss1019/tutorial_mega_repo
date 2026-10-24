
import 'package:budget_app/model/receipt_line.dart';
import 'package:budget_app/service/domain_service.dart';

class ReceiptLineService extends DomainService<ReceiptLine> {
  ReceiptLineService(super.cache, super.repo);

  Future<ReceiptLine> insert(ReceiptLine line) async {
    cache.add(line);
    return line;
  }

  Future<List<ReceiptLine>> getForReceipt(int receiptId) async {
    return cache.getAllWhere((rl) => rl.receiptId == receiptId);
  }

}
