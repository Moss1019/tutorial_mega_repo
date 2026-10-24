
import 'package:budget_app/model/receipt_line.dart';
import 'package:budget_app/repository/repository.dart';

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

}
