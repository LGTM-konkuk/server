package konkuk.ptal.dto.response;

import konkuk.ptal.entity.Reviewer;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReadReviewerResponse {
    Long id;
    List<String> preferences;
    String bio;
    List<String> tags;
    UserMinimalResponse user;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static ReadReviewerResponse from(Reviewer reviewer) {

        BaseAuditResponse baseAuditResponse = BaseAuditResponse.from(reviewer.getCreatedAt());
        return ReadReviewerResponse.builder()
                .id(reviewer.getId())
                .preferences(reviewer.getPreferences())
                .user(UserMinimalResponse.from(reviewer.getUser().getId(), reviewer.getUser().getName(), reviewer.getUser().getEmail()))
                .bio(reviewer.getBio())
                .tags(reviewer.getTags())
                .createdAt(baseAuditResponse.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
