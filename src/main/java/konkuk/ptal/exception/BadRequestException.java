package konkuk.ptal.exception;


import konkuk.ptal.dto.api.ErrorCode;

public class BadRequestException extends BaseException {
    public BadRequestException(ErrorCode code) {
        super(code);
    }
}
