package konkuk.ptal.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final Integer code;
    private final String message;
    private final Boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    // 생성자
    public ApiResponse(Integer code, String message, Boolean success, T result) {
        this.code = code;
        this.message = message;
        this.success = success;
        this.result = result;
    }

    // 성공 - 코드, 메시지 직접 지정
    public static <T> ApiResponse<T> success(String message, T result) {
        return new ApiResponse<>(200, message, true, result);
    }

    // 성공 - ResponseCode 사용
    public static <T> ApiResponse<T> success(ResponseCode code, T result) {
        return new ApiResponse<>(code.getCode(), code.getMessage(), code.getSuccess(), result);
    }

    // 실패 - 코드, 메시지 직접 지정
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, String message, T result) {
        return new ApiResponse<>(errorCode.getCode(), message, false, result);
    }

    // 실패 - ErrorCode 사용
    public static <T> ApiResponse<T> fail(ErrorCode errorCode, T result) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), errorCode.getSuccess(), result);
    }

}