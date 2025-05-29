package konkuk.ptal.exception;


import konkuk.ptal.dto.api.ErrorCode;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(ErrorCode code) {
        super(code);
    }


}
