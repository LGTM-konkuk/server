package konkuk.ptal.dto.response;

import konkuk.ptal.entity.Reviewee;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // Optional 임포트 추가

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRevieweeResponse {
    private Long id;
    private List<String> preferences;
    private LocalDateTime createdAt; // DTO 필드
    private LocalDateTime updatedAt; // DTO 필드

    public static CreateRevieweeResponse from(Reviewee reviewee) {
        BaseAuditResponse baseAuditResponse = BaseAuditResponse.from(reviewee.getCreatedAt());
        return CreateRevieweeResponse.builder()
                .id(reviewee.getId())
                .preferences(Optional.ofNullable(reviewee.getPreferences()).orElse(List.of())) // null 체크 추가
                .createdAt(baseAuditResponse.getCreatedAt()) // 추가된 필드
                .updatedAt(baseAuditResponse.getUpdatedAt()) // 추가된 필드
                .build();
    }
}