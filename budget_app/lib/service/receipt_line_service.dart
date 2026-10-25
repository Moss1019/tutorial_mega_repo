
import 'package:budget_app/model/receipt_line.dart';
import 'package:budget_app/repository/repository.dart';
import 'package:budget_app/result/op_result.dart';
import 'package:budget_app/service/domain_service.dart';

class ReceiptLineService extends DomainService<ReceiptLine> {
  ReceiptLineService(super.cache, super.repo);

  Future<OpResult<ReceiptLine>> insert(ReceiptLine line) async {
    cache.add(line);
    return OpResult.success(line);
  }

  Future<OpResult<List<ReceiptLine>>> getForReceipt(int receiptId) async {
    final lines = cache.getAllWhere((rl) => rl.receiptId == receiptId);
    return OpResult.success(lines);
  }
}
