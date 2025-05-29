package konkuk.ptal.dto.response;

import konkuk.ptal.entity.Reviewee;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReadRevieweeResponse {
    Long id;
    List<String> preferences;
    UserMinimalResponse user;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static ReadRevieweeResponse from(Reviewee reviewee) {

        return ReadRevieweeResponse.builder()
                .id(reviewee.getId())
                .preferences(reviewee.getPreferences())
                .user(UserMinimalResponse.from(reviewee.getUser().getId(), reviewee.getUser().getName(), reviewee.getUser().getEmail()))
                .build();
    }
}
