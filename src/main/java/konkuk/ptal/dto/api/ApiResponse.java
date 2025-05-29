package konkuk.ptal.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    // 생성자
    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    // 성공 - 코드, 메시지 직접 지정
    public static <T> ApiResponse<T> success(String message, T result) {
        return new ApiResponse<>(message, result);
    }

    // 실패 - 코드, 메시지 직접 지정
    public static <T> ApiResponse<T> fail(String message, T result) {
        return new ApiResponse<>(message, result);
    }

}