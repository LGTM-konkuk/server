package konkuk.ptal.service;

import konkuk.ptal.dto.api.ErrorCode;
import konkuk.ptal.dto.request.CreateReviewerRequestDto;
import konkuk.ptal.entity.Reviewer;
import konkuk.ptal.entity.User;
import konkuk.ptal.exception.BadRequestException;
import konkuk.ptal.repository.UserRepository;
import konkuk.ptal.repository.ReviewerRepository;
import konkuk.ptal.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReviewerRepository reviewerRepository;

    @Transactional
    public Reviewer registerReviewer(CreateReviewerRequestDto dto, Long authenticatedUserId) {
        // 1. 인증된 사용자와 요청된 사용자 ID가 일치하는지 확인
        if (!dto.getUserId().equals(authenticatedUserId)) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 2. 사용자 조회
        User user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));

        // 3. 이미 리뷰어인지 확인
        if (Role.REVIEWER.equals(user.getRole())) {
            throw new BadRequestException(ErrorCode.ALREADY_REVIEWER);
        }

        // 4. 리뷰어 엔티티 생성
        Reviewer reviewer = Reviewer.builder()
                .user(user)
                .expertise(dto.getExpertise())
                .bio(dto.getBio())
                .tags(dto.getTags())
                .build();

        // 5. 사용자 역할 업데이트
        user.updateRole(Role.REVIEWER);
        userRepository.save(user);

        // 6. 리뷰어 정보 저장 및 반환
        return reviewerRepository.save(reviewer);
    }

    @Transactional(readOnly = true)
    public Reviewer getReviewer(Long id) {
        return reviewerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorCode.REVIEWER_NOT_FOUND));
    }

    @Transactional
    public Reviewer updateReviewer(Long id, CreateReviewerRequestDto dto, Long authenticatedUserId) {
        Reviewer reviewer = getReviewer(id);
        
        // 리뷰어 본인만 수정 가능
        if (!reviewer.getUser().getId().equals(authenticatedUserId)) {
            throw new BadRequestException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        reviewer.setExpertise(dto.getExpertise());
        reviewer.setBio(dto.getBio());
        reviewer.setTags(dto.getTags());

        return reviewerRepository.save(reviewer);
    }
}
