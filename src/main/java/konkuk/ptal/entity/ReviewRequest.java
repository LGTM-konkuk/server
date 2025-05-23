package konkuk.ptal.entity;

import jakarta.persistence.*;
import konkuk.ptal.domain.enums.ReviewRequestStatus;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private Reviewee reviewee; // The user who is requesting the review

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private Reviewer reviewer; // The user who is being asked to review

    @Column(name = "github_link")
    private String githubLink;

    @Lob
    @Column(name = "request_details", columnDefinition = "TEXT")
    private String requestDetails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewRequestStatus status = ReviewRequestStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "reviewRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Review review; // If a review is created for this request

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}