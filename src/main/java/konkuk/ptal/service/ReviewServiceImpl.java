package konkuk.ptal.service;

import konkuk.ptal.domain.UserPrincipal;
import konkuk.ptal.domain.enums.ReviewCommentType;
import konkuk.ptal.domain.enums.ReviewSubmissionStatus;
import konkuk.ptal.domain.enums.ReviewSubmissionType;
import konkuk.ptal.dto.request.CreateReviewCommentRequest;
import konkuk.ptal.dto.request.CreateReviewSubmissionRequest;
import konkuk.ptal.dto.response.ListReviewSubmissionResponse;
import konkuk.ptal.dto.response.ReadReviewSubmissionResponse;
import konkuk.ptal.entity.*;
import konkuk.ptal.exception.BadRequestException;
import konkuk.ptal.exception.EntityNotFoundException;
import konkuk.ptal.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;


import static konkuk.ptal.dto.api.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {
    private final ReviewSubmissionRepository reviewSubmissionRepository;
    private final CodeFileRepository codeFileRepository;
    private final ReviewCommentRepository reviewCommentRepository;
    private final UserRepository userRepository;
    private final RevieweeRepository revieweeRepository;
    private final ReviewerRepository reviewerRepository;
    private final IFileService fileService;

    @Override
    @Transactional
    public ReviewSubmission createReviewSubmission(CreateReviewSubmissionRequest request, UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        Reviewee reviewee = revieweeRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
        Reviewer reviewer = reviewerRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        ReviewSubmission reviewSubmission = ReviewSubmission.createReviewSubmission(ReviewSubmissionStatus.PENDING, reviewer,reviewee, request);
        reviewSubmission = reviewSubmissionRepository.save(reviewSubmission);

        return reviewSubmission;
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewSubmission getReviewSubmission(Long submissionId, UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        return reviewSubmissionRepository
                .findByIdAndReviewee_User_IdOrIdAndReviewer_User_Id(
                        submissionId, userId,
                        submissionId, userId
                )
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
    }

    @Override
    public Page<ReviewSubmission> getReviewSubmissions(ReviewSubmissionType type, int page, int size, UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewSubmission> reviewSubmissionPage;
        switch (type) {
            case SENT:
                Reviewee revieweeForSent = revieweeRepository.findByUser_Id(userId)
                        .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
                reviewSubmissionPage = reviewSubmissionRepository.findByReviewee(revieweeForSent, pageable);
                break;
            case RECEIVED:
                Reviewer reviewerForReceived = reviewerRepository.findByUser_Id(userId)
                        .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
                reviewSubmissionPage = reviewSubmissionRepository.findByReviewer(reviewerForReceived, pageable);
                break;
            case ALL:
            default:
                reviewSubmissionPage = reviewSubmissionRepository.findByReviewee_User_IdOrReviewer_User_Id(userId, userId, pageable);
                break;
        }
        return reviewSubmissionPage;
    }

    @Override
    public ReviewSubmission cancelReviewSubmission(Long submissionId, UserPrincipal userPrincipal) {
        Long userId = userPrincipal.getUserId();

        ReviewSubmission reviewSubmission = reviewSubmissionRepository
                .findByIdAndReviewee_User_Id(submissionId, userId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        if (reviewSubmission.getStatus() != ReviewSubmissionStatus.PENDING) {
            throw new BadRequestException(SUBMISSION_CANCEL_UNAVAILABLE);
        }

        reviewSubmission.setStatus(ReviewSubmissionStatus.CANCELED);
        ReviewSubmission updatedSubmission = reviewSubmissionRepository.save(reviewSubmission);

        return updatedSubmission;
    }

    @Override
    @Transactional
    public ReviewComment createReviewComment(Long submissionId, CreateReviewCommentRequest request) {
        ReviewSubmission reviewSubmission = reviewSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        CodeFile codeFile = null;
        ReviewCommentType commentType;

        if (request.getCodeFileId() != null && request.getLineNumber() != null) {
            codeFile = codeFileRepository.findById(request.getCodeFileId())
                    .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
            if (codeFile.getSessionId().getId() != submissionId) {
                throw new BadRequestException(FILE_NOT_BELONG_TO_SESSION);
            }
            commentType = ReviewCommentType.CODE_COMMENT;
        } else {
            commentType = ReviewCommentType.SESSION_COMMENT;
        }
        ReviewComment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = reviewCommentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
            if (parentComment.getReviewSubmission().getId() != submissionId) {
                throw new BadRequestException(PARENT_COMMENT_NOT_BELONG_TO_SESSION);
            }
        }
        ReviewComment comment = ReviewComment.createReviewComment(reviewSubmission,parentComment,codeFile,user,commentType,request);
        return reviewCommentRepository.save(comment);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ReviewComment> getReviewComments(Long submissionId, Long codeFileId) {
        reviewSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        if (codeFileId != null) {
            return reviewCommentRepository.findByReviewSubmissionIdAndCodeFileIdAndCommentType(submissionId, codeFileId, ReviewCommentType.CODE_COMMENT);
        } else {
            return reviewCommentRepository.findByReviewSubmissionIdAndCommentTypeAndCodeFileIsNull(submissionId, ReviewCommentType.SESSION_COMMENT);
        }
    }

    @Override
    @Transactional
    public ReviewComment updateReviewComment(String commentId, CreateReviewCommentRequest request) {
        ReviewComment comment = reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

        if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
            comment.setContent(request.getContent());
        }
        return reviewCommentRepository.save(comment);
    }

    @Override
    @Transactional
    public boolean deleteReviewComment(String commentId) {
        Optional<ReviewComment> optionalComment = reviewCommentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            ReviewComment comment = optionalComment.get();
            reviewCommentRepository.delete(comment);
            return true;
        }
        return false;
    }
}
