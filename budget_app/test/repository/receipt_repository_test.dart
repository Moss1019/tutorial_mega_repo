
import 'package:budget_app/model/receipt.dart';
import 'package:budget_app/repository/receipt_repository.dart';
import 'package:budget_app/result/op_result.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:sqflite_common_ffi/sqflite_common_ffi.dart';

void main() {
  late Database db;
  late ReceiptRepository sut;

  setUpAll(() {
    sqfliteFfiInit();
    databaseFactory = databaseFactoryFfi;
  });

  setUp(() async {
    db = await openDatabase(":memory:");
    sut = ReceiptRepository();
    await sut.init(db);
    expect(sut.inError, false);
  });

  test("Insert and retrieve receipt", () async {
    final testReceipt = Receipt(1, DateTime.now(), 0.0);
    final insertRes = await sut.insert(testReceipt);

    expect(sut.inError, false);
    expect(insertRes.successful, true);
    expect(insertRes.content, testReceipt);

    final getRes = await sut.get(1);
    expect(getRes.successful, true);
    expect(getRes.content, testReceipt);
  });

  test("Get non-existent receipt returns failed result", () async {
    final getRes = await sut.get(999);
    expect(getRes.successful, false);
    expect(getRes.error, ErrorCode.e03);
  });
}
