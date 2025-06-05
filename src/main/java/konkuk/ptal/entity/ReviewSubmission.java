package konkuk.ptal.entity;

import jakarta.persistence.*;
import konkuk.ptal.domain.enums.ReviewRequestStatus;
import konkuk.ptal.dto.request.CreateReviewSessionRequest;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewSubmission {

    @Id
    @Column(name = "review_session_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private Reviewee reviewee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Reviewer reviewer;

    @Column(nullable = false)
    private String githubLink;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String absolutePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewRequestStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();

    }

    public static ReviewSubmission createReviewSession(String absolutePath, ReviewRequestStatus status, Reviewer reviewer, Reviewee reviewee, CreateReviewSessionRequest dto){
        return ReviewSubmission.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .reviewee(reviewee)
                .reviewer(reviewer)
                .githubLink(dto.getGithubLink())
                .absolutePath(absolutePath)
                .status(status)
                .build();
    }
}
