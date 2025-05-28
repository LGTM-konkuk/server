package konkuk.ptal.dto.response;

import konkuk.ptal.entity.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReadReviewResponse {
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Long id;
    ReadReviewerResponse reviewer;
    ReadRevieweeResponse reviewee;
    Long reviewSubmissionId;
    String reviewContent;

    public static ReadReviewResponse from(Review review, Reviewee reviewee, Reviewer reviewer) {
        BaseAuditResponse baseAuditResponse = BaseAuditResponse.from(review.getCreatedAt());
        return ReadReviewResponse.builder()
                .id(review.getId())
                .reviewee(ReadRevieweeResponse.from(reviewee))
                .reviewer(ReadReviewerResponse.from(reviewer))
                .reviewSubmissionId(review.getReviewRequest().getId())
                .reviewContent(review.getReviewContent())
                .createdAt(baseAuditResponse.getCreatedAt())
                .updatedAt(baseAuditResponse.getUpdatedAt())
                .build();
    }
}
