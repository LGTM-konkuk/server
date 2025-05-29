package konkuk.ptal.dto.response;

import konkuk.ptal.entity.Reviewer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReviewerResponse {
    private Long id;
    private List<String> preferences;
    private List<String> tags;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CreateReviewerResponse from(Reviewer reviewer) {
        BaseAuditResponse baseAuditResponse = BaseAuditResponse.from(reviewer.getCreatedAt());
        return CreateReviewerResponse.builder()
                .id(reviewer.getId())
                .preferences(reviewer.getPreferences())
                .tags(reviewer.getTags())
                .bio(reviewer.getBio())
                .createdAt(baseAuditResponse.getCreatedAt())
                .updatedAt(baseAuditResponse.getUpdatedAt())
                .build();
    }
}
