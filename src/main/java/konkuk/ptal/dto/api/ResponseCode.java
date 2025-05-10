package konkuk.ptal.dto.api;

import lombok.Getter;


    @Getter
    public enum ResponseCode {
        MEMBER_REGISTER_SUCCESS(201, true, "회원가입 성공"),
        LOGIN_SUCCESS(200, true, "로그인 성공"),
        LOGOUT_SUCCESS(200, true, "로그아웃 성공"),
        DATA_RETRIEVED(200, true, "데이터 조회 성공");

        private final Integer code;
        private final Boolean success;
        private final String message;

        ResponseCode(Integer code, Boolean success, String message) {
            this.code = code;
            this.success = success;
            this.message = message;
        }
    }

