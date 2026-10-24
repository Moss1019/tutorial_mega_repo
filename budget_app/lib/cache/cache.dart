
abstract class Cache<T> {
  final Set<T> _data = {};
  Set<T> get data => _data;

  void add(T item) {
    if(data.contains(item)) {
      data.remove(item);
    }
    data.add(item);
  }

  List<T> getAll() {
    return data.toList();
  }

  List<T> getAllWhere(bool Function(T) conditionCheck) {
    return data.where(conditionCheck).toList();
  }
}
