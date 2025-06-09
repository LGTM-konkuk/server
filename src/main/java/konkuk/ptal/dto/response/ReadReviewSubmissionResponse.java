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
    ProjectFileSystemResponse fileSystem;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static ReadReviewSubmissionResponse from(ReviewSubmission reviewSubmission, ProjectFileSystemResponse fileSystem) {

        BaseAuditResponse baseAuditResponse = BaseAuditResponse.from(reviewSubmission.getCreatedAt());
        return ReadReviewSubmissionResponse.builder()
                .id(reviewSubmission.getId())
                .reviewer(reviewSubmission.getReviewer() != null ? ReadReviewerResponse.from(reviewSubmission.getReviewer()) : null)
                .reviewee(ReadRevieweeResponse.from(reviewSubmission.getReviewee()))
                .gitUrl(reviewSubmission.getGitUrl())
                .branch(reviewSubmission.getBranch())
                .requestDetails(reviewSubmission.getRequestDetails())
                .status(reviewSubmission.getStatus())
                .createdAt(baseAuditResponse.getCreatedAt())
                .updatedAt(baseAuditResponse.getUpdatedAt())
                .fileSystem(fileSystem)
                .build();
    }
}
