package konkuk.ptal.entity;

import jakarta.persistence.*;
import konkuk.ptal.domain.enums.ReviewSubmissionStatus;
import konkuk.ptal.dto.request.CreateReviewSubmissionRequest;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSubmission {

    @Id
    @Column(name = "review_submission_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private Reviewee reviewee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private Reviewer reviewer;

    @Column(nullable = false)
    private String gitUrl;

    @Column(nullable = false)
    private String branch;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String requestDetails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewSubmissionStatus status;

    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;

    @Column(nullable = false)
    LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();

    }

    public static ReviewSubmission createReviewSubmission(ReviewSubmissionStatus status, Reviewer reviewer, Reviewee reviewee, CreateReviewSubmissionRequest dto) {
        return ReviewSubmission.builder()
                .requestDetails(dto.getRequestDetails())
                .reviewee(reviewee)
                .reviewer(reviewer)
                .gitUrl(dto.getGitUrl())
                .branch(dto.getBranch())
                .status(status)
                .build();
    }
}
