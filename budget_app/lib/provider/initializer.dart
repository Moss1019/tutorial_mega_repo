
import 'package:budget_app/repository/repository.dart';
import 'package:sqflite/sqflite.dart';

class Initializer {
  final List<Repository> _repos;

  const Initializer(this._repos);

  Future<void> init() async {
    final db = await openDatabase("budget_db");
    await Future.wait(_repos.map((r) => r.init(db)));
  }
}
