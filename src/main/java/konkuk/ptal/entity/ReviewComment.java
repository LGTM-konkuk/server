package konkuk.ptal.entity;

import jakarta.persistence.*;
import konkuk.ptal.domain.enums.ReviewCommentType;
import konkuk.ptal.dto.request.CreateReviewCommentRequest;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewComment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name="review_session_id", nullable=false)
    private ReviewSubmission reviewSubmission;

    @ManyToOne
    @JoinColumn(name = "codefile_id")
    private CodeFile codeFile;

    private int lineNumber;

    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewCommentType commentType;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private ReviewComment parentComment;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public static ReviewComment createReviewComment(ReviewSubmission reviewSubmission, ReviewComment parentComment, CodeFile codeFile, User user, ReviewCommentType commentType, CreateReviewCommentRequest dto){
        return ReviewComment.builder()
                .reviewSubmission(reviewSubmission)
                .codeFile(codeFile)
                .lineNumber(dto.getLineNumber())
                .commentType(commentType)
                .parentComment(parentComment)
                .user(user)
                .content(dto.getContent())
                .build();
    }
}
