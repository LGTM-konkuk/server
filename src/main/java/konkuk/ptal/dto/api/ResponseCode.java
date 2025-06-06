package konkuk.ptal.dto.api;

import lombok.Getter;


@Getter
public enum ResponseCode {
    OK("OK"),
    MEMBER_REGISTER_SUCCESS("회원가입 성공"),
    LOGIN_SUCCESS("로그인 성공"),
    LOGOUT_SUCCESS("로그아웃 성공"),
    DATA_RETRIEVED("데이터 조회 성공"),
    DATA_UPDATE_SUCCESS("데이터 수정 성공"),
    REVIEWER_REGISTER_SUCCESS("리뷰어 등록 성공"),
    REVIEWEE_REGISTER_SUCCESS("리뷰이 등록 성공"),
    REVIEW_CREATED("리뷰 생성 성공"),
    REVIEW_UPDATED("리뷰 업데이트 성공"),
    TOKEN_REFRESH_SUCCESS("토큰 리프레시 성공"),
    REVIEW_SUBMISSION_CREATED("리뷰 요청 성공"),
    REVIEW_SUBMISSION_CANCELED("리뷰 요청 취소 성공");

    private final String message;

    ResponseCode(String message) {
        this.message = message;
    }
}