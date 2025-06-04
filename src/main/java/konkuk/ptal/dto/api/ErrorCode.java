package konkuk.ptal.dto.api;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_JWT("유효하지 않은 토큰입니다."),
    DUPLICATED_EMAIL("중복 이메일입니다."),
    ALREADY_REVIEWER("이미 등록된 리뷰어입니다."),
    ALREADY_REVIEWEE("이미 등록된 리뷰이입니다."),
    BAD_REQUEST("잘못된 요청입니다."),
    PASSWORD_NOT_AVAILABLE("사용할 수 없는 비밀번호입니다."),
    PASSWORD_NOT_EQUAL("비밀번호가 일치하지 않습니다."),
    EMAIL_NOT_AVAILABLE("사용할 수 없는 이메일입니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    ACCESS_DENIED("접근 권한이 없습니다."),
    INTERNAL_SERVER_ERROR("서버 내부 에러."),
    REPO_NAME_EXTRACTION_FAILED("레포지토리를 찾을 수 없습니다."),
    REPO_CLONE_FAILED("레포지토리를 클론할 수 없습니다."),
    FILE_SYSTEM_ERROR("파일시스템 에러가 발생했습니다."),
    INVALID_REPO_PATH("제공된 절대경로로 파일을 찾을 수 없습니다."),
    FILE_LIST_ERROR("파일 리스트를 가져오는 데 에러가 발생했습니다.");


    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
