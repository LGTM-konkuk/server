package konkuk.ptal.repository;

import konkuk.ptal.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByReviewSubmissionId(Long reviewSubmissionId);

    Optional<Review> findByIdAndReviewSubmission_Reviewee_User_IdOrIdAndReviewSubmission_Reviewer_User_Id(
            Long reviewId1, Long revieweeUserId,
            Long reviewId2, Long reviewerUserId
    );

    Optional<Review> findByIdAndReviewSubmission_Reviewer_User_Id(Long reviewId, Long reviewerUserId);

    Page<Review> findByReviewSubmissionIdAndReviewSubmission_Reviewee_User_IdOrReviewSubmission_Reviewer_User_Id(
            Long reviewSubmissionId1, Long revieweeUserId,
            Long reviewSubmissionId2, Long reviewerUserId,
            Pageable pageable
    );

    Page<Review> findByReviewSubmission_Reviewer_User_Id(Long reviewerUserId, Pageable pageable);

    Page<Review> findByReviewSubmission_Reviewee_User_Id(Long revieweeUserId, Pageable pageable);

    Page<Review> findByReviewSubmission_Reviewee_User_IdOrReviewSubmission_Reviewer_User_Id(
            Long revieweeUserId, Long reviewerUserId,
            Pageable pageable
    );
}
