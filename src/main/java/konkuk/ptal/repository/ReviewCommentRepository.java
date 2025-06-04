package konkuk.ptal.repository;

import konkuk.ptal.domain.enums.ReviewCommentType;
import konkuk.ptal.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    ReviewComment findByReviewSessionIdAndCodeFileIdAndCommentType(Long sessionId, Long codeFileId, ReviewCommentType reviewCommentType);

    ReviewComment findByReviewSessionIdAndCommentTypeAndCodeFileIsNull(Long sessionId, ReviewCommentType reviewCommentType);
}
