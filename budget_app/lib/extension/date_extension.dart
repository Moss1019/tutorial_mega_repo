
extension DateExtension on DateTime {
  String formatDate() {
    final monthStr = month.toString().padLeft(2, "0");
    final dayStr = day.toString().padLeft(2, "0");
    return "$year/$monthStr/$dayStr";
  }
}
