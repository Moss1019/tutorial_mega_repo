
import 'package:budget_app/model/receipt_line.dart';
import 'package:budget_app/repository/repository.dart';
import 'package:budget_app/result/op_result.dart';

class ReceiptLineRepository extends Repository<ReceiptLine> {
  static final fld_id = "id";
  static final fld_receiptId = "receiptId";
  static final fld_label = "label";
  static final fld_price = "price";

  @override
  String get createStatement => '''
  create table if not exists $table (
  $fld_id text primary key,
  $fld_receiptId int,
  $fld_label text,
  $fld_price real
  )
  ''';

  @override
  String get table => "receipt_line";

  Future<OpResult<ReceiptLine>> insert(ReceiptLine line) async {
    try {
      await db.insert(table, {
        fld_id: line.id,
        fld_receiptId: line.receiptId,
        fld_label: line.label,
        fld_price: line.price,
      }, conflictAlgorithm: .replace);
      return OpResult.success(line);
    } catch (ex) {
      handleError(ex);
      return OpResult.failedWith("Failed to insert receipt line: $ex", ErrorCode.e02);
    }
  }

  Future<OpResult<List<ReceiptLine>>> getForReceipt(int receiptId) async {
    try {
      final res = await db.query(
        table,
        where: "$fld_receiptId = ?",
        whereArgs: [receiptId],
      );
      final lines = <ReceiptLine>[];
      for (final r in res) {
        lines.add(
          ReceiptLine(
            r[fld_id] as String,
            r[fld_receiptId] as int,
            r[fld_label] as String,
            r[fld_price] as double,
          ),
        );
      }
      return OpResult.success(lines);
    } catch (ex) {
      handleError(ex);
      return OpResult.failedWith("Failed to get receipt lines: $ex", ErrorCode.e02);
    }
  }
}
