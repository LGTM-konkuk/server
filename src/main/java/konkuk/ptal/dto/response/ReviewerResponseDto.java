package konkuk.ptal.dto.response;

import konkuk.ptal.entity.Reviewer;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewerResponseDto {
    private Long id;
    private String expertise;
    private List<String> tags;
    private String bio;

    public static ReviewerResponseDto from(Reviewer reviewer) {
        return ReviewerResponseDto.builder()
                .id(reviewer.getId())
                .expertise(reviewer.getExpertise())
                .tags(reviewer.getTags())
                .bio(reviewer.getBio())
                .build();
    }
} 