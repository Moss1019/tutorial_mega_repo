
import 'dart:async';

class MessageBus {
  final _controller = StreamController<MapEntry<String, Object?>>();

  final _state = <String, Object?>{};

  void emit(String key, Object? value) {
    _state[key] = value;
    _controller.add(MapEntry(key, value));
  }

  Object? get(String key) {
    return _state[key];
  }

  StreamSubscription<MapEntry<String, Object?>> sub(void Function (MapEntry<String, Object?>) handler) {
    return _controller.stream.listen(handler);
  }
}
