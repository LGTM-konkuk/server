package konkuk.ptal.exception;

import konkuk.ptal.dto.api.ErrorCode;

public class EntityNotFoundException extends BaseException{
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
