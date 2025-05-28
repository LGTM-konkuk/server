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
public class ReviewerResponse {
    private Long id;
    private String expertise;
    private List<String> tags;
    private String bio;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ReviewerResponse from(Reviewer reviewer) {
        return ReviewerResponse.builder()
                .id(reviewer.getId())
                .expertise(reviewer.getExpertise())
                .tags(reviewer.getTags())
                .bio(reviewer.getBio())
                .createdAt(reviewer.getCreatedAt())
                .updatedAt(reviewer.getUpdatedAt())
                .build();
    }
}
