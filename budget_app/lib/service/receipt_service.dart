
import 'package:budget_app/model/receipt.dart';
import 'package:budget_app/repository/receipt_repository.dart';
import 'package:budget_app/repository/repository.dart';
import 'package:budget_app/result/op_result.dart';
import 'package:budget_app/service/domain_service.dart';

class ReceiptService extends DomainService<Receipt> {
  int? _lastReadReceiptCount;

  ReceiptService(super.cache, super.repo);

  Future<OpResult<Receipt>> insert(Receipt r) async {
    cache.add(r);

    final insertRes = await (repo as ReceiptRepository).insert(r);
    if (!insertRes.successful) {
      return OpResult.failedWith(insertRes.message ?? "Failed to insert receipt", insertRes.error);
    }

    return OpResult.success(r);
  }

  Future<OpResult<List<Receipt>>> getAll() async {
    if (_lastReadReceiptCount == null) {
      final repoRes = await (repo as ReceiptRepository).getAll();
      if (!repoRes.successful) {
        return OpResult.failedWith(repoRes.message ?? "Failed to get all receipts", repoRes.error);
      }
      _lastReadReceiptCount = repoRes.content?.length ?? 0;
      for (final r in repoRes.content ?? []) {
        cache.add(r);
      }
    }

    return OpResult.success(cache.getAll());
  }

  Future<OpResult<Receipt>> get(int id) async {
    if (_lastReadReceiptCount == null) {
      await getAll();
    }
    final res = cache.getAllWhere((r) => r.id == id);
    if (res.isEmpty) {
      return OpResult.failedWith("Receipt with id $id not found in cache", ErrorCode.e03);
    }
    return OpResult.success(res[0]);
  }
}
