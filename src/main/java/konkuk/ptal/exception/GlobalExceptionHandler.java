package konkuk.ptal.exception;

import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequestException(BadRequestException ex) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<?>> handleUnauthorizedException(UnauthorizedException ex) {
        return buildResponse(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGenericException(Exception ex) {
        // 예외 로깅
        log.error("Unexpected error occurred: ", ex);

        // BaseException 생성 시 예외 메시지와 ErrorCode 설정
        BaseException baseException = new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);

        // API 응답 생성
        ApiResponse<?> response = ApiResponse.fail(baseException.getErrorCode(), baseException.getMessage(), null);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse<?>> buildResponse(BaseException ex, HttpStatus status) {
        ApiResponse<?> response = ApiResponse.fail(ex.getErrorCode(), ex.getMessage(), null);
        return new ResponseEntity<>(response, status);
    }
}
