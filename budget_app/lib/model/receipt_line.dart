
class ReceiptLine {
  final String id;

  final int receiptId;

  final String label;

  final double price;

  @override
  int get hashCode => id.hashCode;

  const ReceiptLine(this.id, this.receiptId, this.label, this.price);

  @override
  bool operator ==(Object other) {
    if(other is! ReceiptLine) return false;
    return id == other.id;
  }
}
