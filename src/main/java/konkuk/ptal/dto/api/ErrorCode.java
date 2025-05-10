package konkuk.ptal.dto.api;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_JWT(401, false, "유효하지 않은 토큰입니다."),
    DUPLICATED_EMAIL(409, false, "중복 이메일입니다."),
    BAD_REQUEST(400, false, "잘못된 요청입니다."),
    PASSWORD_NOT_AVAILABLE(400, false, "사용할 수 없는 비밀번호입니다."),
    PASSWORD_NOT_EQUAL(400, false, "비밀번호가 일치하지 않습니다."),
    EMAIL_NOT_AVAILABLE(400, false, "사용할 수 없는 이메일입니다."),
    USER_NOT_FOUND(404, false, "사용자를 찾을 수 없습니다."),
    ACCESS_DENIED(403, false, "접근 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(500, false, "서버 내부 에러.");

    private final Integer code;
    private final Boolean success;
    private final String message;

    ErrorCode(Integer code, Boolean success, String message) {
        this.code = code;
        this.success = success;
        this.message = message;
    }
}
