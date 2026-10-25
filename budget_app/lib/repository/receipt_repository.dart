import 'package:budget_app/model/receipt.dart';
import 'package:budget_app/repository/repository.dart';
import 'package:budget_app/result/op_result.dart';

class ReceiptRepository extends Repository<Receipt> {
  static final fld_id = "id";
  static final fld_date = "date";
  static final fld_total = "total";

  @override
  String get createStatement =>
      '''
  create table if not exists $table (
  $fld_id int primary key,
  $fld_date text,
  $fld_total real
  )
  ''';

  @override
  String get table => "receipt";

  Future<OpResult<Receipt>> insert(Receipt r) async {
    try {
      await db.insert(table, {
        fld_id: r.id,
        fld_date: r.date.toIso8601String(),
        fld_total: r.total,
      }, conflictAlgorithm: .replace);
      return OpResult.success(r);
    } catch (ex) {
      handleError(ex);
      return OpResult.failedWith("Failed to insert receipt: $ex", ErrorCode.e02);
    }
  }

  Future<OpResult<List<Receipt>>> getAll() async {
    try {
      final res = await db.query(table);
      final receipts = <Receipt>[];
      for (final r in res) {
        receipts.add(
          Receipt(
            r[fld_id] as int,
            DateTime.parse(r[fld_date] as String),
            r[fld_total] as double,
          ),
        );
      }
      return OpResult.success(receipts);
    } catch (ex) {
      handleError(ex);
      return OpResult.failedWith("Failed to get all receipts: $ex", ErrorCode.e02);
    }
  }

  Future<OpResult<Receipt>> get(int id) async {
    try {
      final res = await db.query(table, where: "$fld_id = ?", whereArgs: [id]);
      if (res.isEmpty) {
        return OpResult.failedWith("Receipt with id $id not found", ErrorCode.e03);
      }
      final r = res[0];
      return OpResult.success(
        Receipt(
          r[fld_id] as int,
          DateTime.parse(r[fld_date] as String),
          r[fld_total] as double,
        ),
      );
    } catch (ex) {
      handleError(ex);
      return OpResult.failedWith("Failed to get receipt: $ex", ErrorCode.e02);
    }
  }
}
