package konkuk.ptal.repository;

import konkuk.ptal.domain.enums.ReviewCommentType;
import konkuk.ptal.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, String> {
    List<ReviewComment> findByReviewSubmissionIdAndCodeFileIdAndCommentType(Long submissionId, Long codeFileId, ReviewCommentType reviewCommentType);

    List<ReviewComment> findByReviewSubmissionIdAndCommentTypeAndCodeFileIsNull(Long submissionId, ReviewCommentType reviewCommentType);
    
    // 특정 리뷰 세션의 모든 댓글을 조회 (답글 포함)
    List<ReviewComment> findByReviewSubmissionId(Long submissionId);
    
    // 특정 코드 파일의 모든 댓글을 조회 (답글 포함)
    List<ReviewComment> findByReviewSubmissionIdAndCodeFileId(Long submissionId, Long codeFileId);
}
