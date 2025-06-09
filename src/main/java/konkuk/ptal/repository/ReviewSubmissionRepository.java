package konkuk.ptal.repository;

import konkuk.ptal.domain.enums.ReviewSubmissionStatus;
import konkuk.ptal.entity.ReviewSubmission;
import konkuk.ptal.entity.Reviewee;
import konkuk.ptal.entity.Reviewer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewSubmissionRepository extends JpaRepository<ReviewSubmission, Long> {

    Optional<ReviewSubmission> findByIdAndReviewer_User_IdOrIdAndReviewerIsNullAndStatus(
            Long id, Long reviewerUserId, Long id2, ReviewSubmissionStatus pendingStatus
    );

    @Query("SELECT rs FROM ReviewSubmission rs " +
            "LEFT JOIN rs.reviewer rr " + // Reviewer 조인
            "WHERE rr.user.id = :userId " + // 조건 1: 나에게 할당된 리뷰
            "OR (rr IS NULL AND rs.status = :pendingStatus)") // 조건 2: Reviewer가 null이고 PENDING인 리뷰
    Page<ReviewSubmission> findAllSubmissionsForReviewer(@Param("userId") Long userId,
                                                         @Param("pendingStatus") ReviewSubmissionStatus pendingStatus,
                                                         Pageable pageable);
    Optional<ReviewSubmission> findByIdAndReviewee_User_Id(Long submissionId, Long revieweeUserId);

    Page<ReviewSubmission> findByReviewee(Reviewee reviewee, Pageable pageable);

    Page<ReviewSubmission> findByReviewer(Reviewer reviewer, Pageable pageable);

    Page<ReviewSubmission> findByReviewee_User_IdOrReviewer_User_Id(Long revieweeUserId, Long reviewerUserId, Pageable pageable);

    Optional<ReviewSubmission> findByIdAndReviewer_User_Id(Long submissionId, Long reviewerUserId);

}
