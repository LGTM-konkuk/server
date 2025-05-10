package konkuk.ptal.exception;


import konkuk.ptal.dto.api.ErrorCode;

public class JsonParsingException extends BaseException {
    public JsonParsingException(ErrorCode code) {
        super(code);
    }
}
