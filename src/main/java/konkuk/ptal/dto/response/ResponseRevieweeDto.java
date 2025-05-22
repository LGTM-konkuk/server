package konkuk.ptal.dto.response;

import konkuk.ptal.entity.Reviewee;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResponseRevieweeDto {
    private Long id;
    private String displayName;
    private List<String> preferences;

    public static ResponseRevieweeDto from(Reviewee reviewee) {
        return ResponseRevieweeDto.builder()
                .id(reviewee.getId())
                .displayName(reviewee.getDisplayName())
                .preferences(reviewee.getPreferences())
                .build();
    }
}