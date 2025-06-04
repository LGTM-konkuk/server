package konkuk.ptal.service;

import konkuk.ptal.dto.request.CreateReviewCommentRequest;
import konkuk.ptal.dto.request.CreateReviewSessionRequest;
import konkuk.ptal.entity.ReviewComment;
import konkuk.ptal.entity.ReviewSession;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements IReviewService {
    @Override
    public ReviewSession createReviewSession(CreateReviewSessionRequest request) {
        return null;
    }

    @Override
    public ReviewSession getReviewSession(Long sessionId) {
        return null;
    }

    @Override
    public ReviewComment createReviewComment(Long sessionId, CreateReviewCommentRequest request) {
        return null;
    }

    @Override
    public ReviewComment getReviewComments(Long sessionId, Long codeFileId) {
        return null;
    }

    @Override
    public ReviewComment updateReviewComment(Long commentId, CreateReviewCommentRequest request) {
        return null;
    }

    @Override
    public boolean deleteReviewComment(Long commentId) {
        return false;
    }
}
