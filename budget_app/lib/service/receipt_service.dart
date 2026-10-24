
import 'package:budget_app/model/receipt.dart';
import 'package:budget_app/repository/receipt_repository.dart';
import 'package:budget_app/service/domain_service.dart';

class ReceiptService extends DomainService<Receipt> {
  int? _lastReadReceiptCount;

  ReceiptService(super.cache, super.repo);

  Future<Receipt?> insert(Receipt r) async {
    cache.add(r);

    final insertRes = await (repo as ReceiptRepository).insert(r);
    if(insertRes == null) {
      return null;
    }

    return r;
  }

  Future<List<Receipt>> getAll() async {
    if(_lastReadReceiptCount == null) {
      final repoRes = await (repo as ReceiptRepository).getAll();
      _lastReadReceiptCount = repoRes.length;
      for(final r in repoRes) {
        cache.add(r);
      }
    }

    return cache.getAll();
  }

  Future<Receipt?> get(int id) async {
    if(_lastReadReceiptCount == null) {
      await getAll();
    }
    final res = cache.getAllWhere((r) => r.id == id);
    if(res.isEmpty) return null;
    return res[0];
  }
}
