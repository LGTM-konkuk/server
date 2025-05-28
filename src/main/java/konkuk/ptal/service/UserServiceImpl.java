package konkuk.ptal.service;

import konkuk.ptal.domain.enums.Role;
import konkuk.ptal.dto.api.ErrorCode;
import konkuk.ptal.dto.request.CreateRevieweeRequest;
import konkuk.ptal.dto.request.CreateReviewerRequest;
import konkuk.ptal.entity.Reviewee;
import konkuk.ptal.entity.Reviewer;
import konkuk.ptal.entity.User;
import konkuk.ptal.exception.BadRequestException;
import konkuk.ptal.repository.RevieweeRepository;
import konkuk.ptal.repository.ReviewerRepository;
import konkuk.ptal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final ReviewerRepository reviewerRepository;
    private final RevieweeRepository revieweeRepository;

    @Transactional
    public Reviewer registerReviewer(CreateReviewerRequest dto) {
        return null;
    }

    @Transactional(readOnly = true)
    public Reviewer getReviewer(Long id) {
        return reviewerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public Reviewer updateReviewer(Long id, CreateReviewerRequest dto, Long authenticatedUserId) {
        Reviewer reviewer = getReviewer(id);

        // 리뷰어 본인만 수정 가능
        if (!reviewer.getUser().getId().equals(authenticatedUserId)) {
            throw new BadRequestException(ErrorCode.INVALID_JWT);
        }

        reviewer.setBio(dto.getBio());
        reviewer.setTags(dto.getTags());

        return reviewerRepository.save(reviewer);
    }

    @Override
    public Reviewee registerReviewee(CreateRevieweeRequest dto) {
        return null;
    }

    @Override
    public Reviewee getReviewee(Long id) {
        return revieweeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public Reviewee updateReviewee(Long id, CreateRevieweeRequest dto, Long authenticatedUserId) {
        Reviewee reviewee = getReviewee(id);

        // 리뷰어 본인만 수정 가능
        if (!reviewee.getUser().getId().equals(authenticatedUserId)) {
            throw new BadRequestException(ErrorCode.INVALID_JWT);
        }
        reviewee.setPreferences(dto.getPreferences());

        return revieweeRepository.save(reviewee);
    }
}
