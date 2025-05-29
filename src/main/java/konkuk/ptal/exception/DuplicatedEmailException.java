package konkuk.ptal.exception;

import konkuk.ptal.dto.api.ErrorCode;

public class DuplicatedEmailException extends BaseException {

    public DuplicatedEmailException(ErrorCode code) {
        super(code);
    }

}
