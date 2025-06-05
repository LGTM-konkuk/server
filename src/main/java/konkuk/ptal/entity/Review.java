package konkuk.ptal.entity;

import jakarta.persistence.*;
import konkuk.ptal.dto.request.CreateReviewRequest;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "reviewer_id")
    private Reviewer reviewer;

    @OneToOne
    @JoinColumn(name = "reviewee_id")
    private Reviewee reviewee;

    @OneToOne
    @JoinColumn(name = "review_submission_id")
    private ReviewSubmission reviewSubmission;

    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;
    @Column(nullable = false)
    LocalDateTime updatedAt;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    String reviewContent;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateConclusion(String reviewContent){
        this.reviewContent = reviewContent;
    }
    public static Review createReview(ReviewSubmission reviewSubmission, CreateReviewRequest dto){
        return Review.builder()
                .reviewer(reviewSubmission.getReviewer())
                .reviewee(reviewSubmission.getReviewee())
                .reviewSubmission(reviewSubmission)
                .reviewContent(dto.getReviewContent())
                .build();
    }

}
