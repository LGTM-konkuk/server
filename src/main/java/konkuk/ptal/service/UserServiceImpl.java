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
    public Reviewer registerReviewer(CreateReviewerRequest dto, Long authenticatedUserId) {
        // 1. 인증된 사용자와 요청된 사용자 ID가 일치하는지 확인
        if (!dto.getUserId().equals(authenticatedUserId)) {
            throw new BadRequestException(ErrorCode.INVALID_JWT);
        }

        // 2. 사용자 조회
        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));

        // 3. 이미 리뷰어인지 확인
        if (Role.REVIEWER.equals(user.getRole())) {
            throw new BadRequestException(ErrorCode.ALREADY_REVIEWER);
        }

        // 4. 리뷰어 엔티티 생성
        Reviewer reviewer = Reviewer.createReviewer(user, dto);

        // 5. 사용자 역할 업데이트
        user.updateRole(Role.REVIEWER);
        userRepository.save(user);

        // 6. 리뷰어 정보 저장 및 반환
        return reviewerRepository.save(reviewer);
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

        reviewer.setExpertise(dto.getExpertise());
        reviewer.setBio(dto.getBio());
        reviewer.setTags(dto.getTags());

        return reviewerRepository.save(reviewer);
    }

    @Override
    public Reviewee registerReviewee(CreateRevieweeRequest dto, Long authenticatedUserId) {
        if (!dto.getUserId().equals(authenticatedUserId)) {
            throw new BadRequestException(ErrorCode.INVALID_JWT);
        }

        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));

        if (Role.REVIEWEE.equals(user.getRole())) {
            throw new BadRequestException(ErrorCode.ALREADY_REVIEWEE);
        }

        Reviewee reviewee = Reviewee.createReviewee(user, dto);

        user.updateRole(Role.REVIEWEE);
        userRepository.save(user);

        return revieweeRepository.save(reviewee);
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

        reviewee.setDisplayName(dto.getDisplayName());
        reviewee.setPreferences(dto.getPreferences());

        return revieweeRepository.save(reviewee);
    }
}
