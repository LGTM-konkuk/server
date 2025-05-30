package konkuk.ptal.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BaseAuditResponse {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BaseAuditResponse from(LocalDateTime createdAt) {
        return BaseAuditResponse.builder()
                .createdAt(createdAt) // 추가된 필드
                .updatedAt(LocalDateTime.now()) // 추가된 필드
                .build();
    }
}
