package konkuk.ptal.dto.response;

import konkuk.ptal.domain.enums.ReviewRequestStatus;
import konkuk.ptal.entity.ReviewRequest;
import konkuk.ptal.entity.Reviewee;
import konkuk.ptal.entity.Reviewer;
import konkuk.ptal.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReadReviewSubmissionResponse {
    Long id;
    ReadRevieweeResponse reviewee;
    ReadReviewerResponse reviewer;
    String gitUrl;
    String requestDetails;
    ReviewRequestStatus status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static ReadReviewSubmissionResponse from(ReviewRequest reviewRequest, Reviewee reviewee, Reviewer reviewer) {
        BaseAuditResponse baseAuditResponse = BaseAuditResponse.from(reviewRequest.getCreatedAt());
        return ReadReviewSubmissionResponse.builder()
                .id(reviewRequest.getId())
                .reviewee(ReadRevieweeResponse.from(reviewee))
                .reviewer(ReadReviewerResponse.from(reviewer))
                .gitUrl(reviewRequest.getGithubLink())
                .requestDetails(reviewRequest.getRequestDetails())
                .status(reviewRequest.getStatus())
                .createdAt(baseAuditResponse.getCreatedAt())
                .updatedAt(baseAuditResponse.getUpdatedAt())
                .build();
    }
}
