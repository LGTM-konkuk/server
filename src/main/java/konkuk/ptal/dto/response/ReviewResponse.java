package konkuk.ptal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private ReviewerResponse reviewer; // 또는 Long reviewerId;
    private RevieweeResponse reviewee; // 또는 Long revieweeId;
    private Long reviewRequestId;
    private String reviewContent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // DB 엔티티를 DTO로 변환하는 정적 팩토리 메서드 (예시)
    // public static ReviewResponseDto from(Review review) {
    //     if (review == null) return null;
    //     return new ReviewResponseDto(
    //         review.getId(),
    //         ReviewerResponseDto.from(review.getReviewer()), // TODO: Reviewer 엔티티와 DTO 매핑
    //         ResponseRevieweeDto.from(review.getReviewee()), // TODO: Reviewee 엔티티와 DTO 매핑
    //         review.getReviewRequestId(), // 또는 review.getReviewRequest().getId()
    //         review.getReviewContent(),
    //         review.getCreatedAt(),
    //         review.getUpdatedAt()
    //     );
    // }
}