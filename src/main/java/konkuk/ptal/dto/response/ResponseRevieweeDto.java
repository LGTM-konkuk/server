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
public class ResponseRevieweeDto {
    private Long id;
    private String displayName;
    private List<String> preferences;
    private LocalDateTime createdAt; // DTO 필드
    private LocalDateTime updatedAt; // DTO 필드

    public static ResponseRevieweeDto from(Reviewee reviewee) {
        if (reviewee == null) {
            return null;
        }
        return ResponseRevieweeDto.builder()
                .id(reviewee.getId())
                .displayName(reviewee.getDisplayName())
                .preferences(Optional.ofNullable(reviewee.getPreferences()).orElse(List.of())) // null 체크 추가
                .createdAt(reviewee.getCreatedAt()) // 추가된 필드
                .updatedAt(reviewee.getUpdatedAt()) // 추가된 필드
                .build();
    }
}