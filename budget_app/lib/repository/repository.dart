
import 'package:budget_app/repository/repository.dart';
import 'package:budget_app/result/op_result.dart';
import 'package:sqflite/sqflite.dart';

abstract class Repository<T> {
  late Database _db;
  Database get db => _db;

  bool _inError = false;
  bool get inError => _inError;

  String _error = "";
  String get error => _error;

  String get createStatement;

  String get table;

  Future<void> init(Database db) async {
    _db = db;
    try {
      await db.rawQuery(createStatement);
    } catch (ex) {
      print(ex);
    }
  }

  Future<void> clear(bool? dropTable) async {
    try {
      await db.rawQuery("drop table $table");
    } catch (ex) {
      print(ex);
    }
  }

  void handleError(Object ex) {
    _inError = true;
    _error = "Repository error: $ex";
  }

  OpResult<void> success() => OpResult.success(null);

  OpResult<void> failed(String message) => OpResult.failed(message);

  OpResult<T> successWithContent(T content) => OpResult.success(content);

  OpResult<List<T>> successWithList(List<T> list) => OpResult.success(list);
}
