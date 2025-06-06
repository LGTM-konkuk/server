package konkuk.ptal.repository;

import konkuk.ptal.entity.ReviewSubmission;
import konkuk.ptal.entity.Reviewee;
import konkuk.ptal.entity.Reviewer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewSubmissionRepository extends JpaRepository<ReviewSubmission, Long> {
    Optional<ReviewSubmission> findByIdAndReviewee_User_IdOrIdAndReviewer_User_Id(
            Long submissionId1, Long revieweeUserId,
            Long submissionId2, Long reviewerUserId
    );
    Optional<ReviewSubmission> findByIdAndReviewee_User_Id(Long submissionId, Long revieweeUserId);
    Page<ReviewSubmission> findByReviewee(Reviewee reviewee, Pageable pageable);

    Page<ReviewSubmission> findByReviewer(Reviewer reviewer, Pageable pageable);

    Page<ReviewSubmission> findByReviewee_User_IdOrReviewer_User_Id(Long revieweeUserId, Long reviewerUserId, Pageable pageable);

    Optional<ReviewSubmission> findByIdAndReviewer_User_Id(Long submissionId, Long reviewerUserId);

}
