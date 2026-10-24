
import 'package:budget_app/model/receipt.dart';
import 'package:budget_app/repository/receipt_repository.dart';
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

  test("Insert an retrieve receipt", () async {
    final testReceipt = Receipt(1, DateTime.now(), 0.0);
    final insertRes = await sut.insert(testReceipt);

    expect(sut.inError, false);
    expect(insertRes, testReceipt);

    final getRes = await sut.get(1);
    expect(getRes, testReceipt);
  });
}
