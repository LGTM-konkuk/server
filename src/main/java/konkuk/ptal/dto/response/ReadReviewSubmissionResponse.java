package konkuk.ptal.dto.response;

import konkuk.ptal.domain.enums.ReviewSubmissionStatus;
import konkuk.ptal.entity.ReviewSubmission;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class ReadReviewSubmissionResponse {
    Long id;
    ReadReviewerResponse reviewer;
    ReadRevieweeResponse reviewee;
    String gitUrl;
    String branch;
    String requestDetails;
    ReviewSubmissionStatus status;
    ProjectFileSystem fileSystem;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static ReadReviewSubmissionResponse from(ReviewSubmission reviewSubmission) {

        BaseAuditResponse baseAuditResponse = BaseAuditResponse.from(reviewSubmission.getCreatedAt());
        return ReadReviewSubmissionResponse.builder()
                .id(reviewSubmission.getId())
                .reviewer(ReadReviewerResponse.from(reviewSubmission.getReviewer()))
                .reviewee(ReadRevieweeResponse.from(reviewSubmission.getReviewee()))
                .gitUrl(reviewSubmission.getGitUrl())
                .branch(reviewSubmission.getBranch())
                .createdAt(baseAuditResponse.getCreatedAt())
                .updatedAt(baseAuditResponse.getUpdatedAt())
                //.fileSystem()
                .build();
    }
}
