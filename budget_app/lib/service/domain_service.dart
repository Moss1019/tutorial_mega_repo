
import 'package:budget_app/cache/cache.dart';
import 'package:budget_app/repository/repository.dart';

abstract class DomainService<T> {
  final Cache<T> _cache;
  Cache<T> get cache => _cache;

  final Repository<T> _repo;
  Repository<T> get repo => _repo;

  const DomainService(this._cache, this._repo);
}
