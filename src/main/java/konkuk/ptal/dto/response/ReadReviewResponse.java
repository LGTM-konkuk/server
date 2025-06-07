package konkuk.ptal.dto.response;

import konkuk.ptal.domain.enums.ReviewSubmissionStatus;
import konkuk.ptal.entity.Review;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReadReviewResponse {
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
    String reviewContent;

    public static ReadReviewResponse from(Review review, ProjectFileSystemResponse fileSystem) {
        return ReadReviewResponse.builder()
                .id(review.getReviewSubmission().getId())
                .reviewContent(review.getReviewContent())
                .reviewer(ReadReviewerResponse.from(review.getReviewSubmission().getReviewer()))
                .reviewee(ReadRevieweeResponse.from(review.getReviewSubmission().getReviewee()))
                .gitUrl(review.getReviewSubmission().getGitUrl())
                .branch(review.getReviewSubmission().getBranch())
                .requestDetails(review.getReviewSubmission().getRequestDetails())
                .status(review.getReviewSubmission().getStatus())
                .fileSystem(fileSystem)
                .createdAt(review.getReviewSubmission().getCreatedAt())
                .updatedAt(review.getReviewSubmission().getUpdatedAt())
                .build();
    }
}
