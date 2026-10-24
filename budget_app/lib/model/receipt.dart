
class Receipt {
  final int id;

  final DateTime date;

  final double total;

  @override
  int get hashCode => id;

  const Receipt(this.id, this.date, this.total);

  @override
  bool operator ==(Object other) {
    if(other is! Receipt) return false;
    return id == other.id;
  }
}
