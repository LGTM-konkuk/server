package konkuk.ptal.exception;


import konkuk.ptal.dto.api.ErrorCode;

public class JsonException extends BaseException {
    public JsonException(ErrorCode code) {
        super(code);
    }
}
