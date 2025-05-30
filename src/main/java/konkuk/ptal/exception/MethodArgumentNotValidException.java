package konkuk.ptal.exception;

import konkuk.ptal.dto.api.ErrorCode;

public class MethodArgumentNotValidException extends BaseException {
    public MethodArgumentNotValidException(ErrorCode errorCode) {
        super(errorCode);
    }
}
