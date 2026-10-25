
enum ErrorCode {
  e01, // general error
  e02, // database error
  e03, // not found error
}

class OpResult<T> {
  final bool successful;

  final T? content;

  final ErrorCode? error;

  final String? message;

  OpResult.success(this.content): successful = true, error = null, message = null;

  OpResult.failed(this.message): successful = false, error = null, content = null;

  OpResult.failedWith(this.message, this.error): successful = false, content = null;
}
