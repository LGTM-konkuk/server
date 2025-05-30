package konkuk.ptal.service;

import konkuk.ptal.domain.enums.Role;
import konkuk.ptal.dto.api.ErrorCode;
import konkuk.ptal.dto.request.CreateRevieweeRequest;
import konkuk.ptal.dto.request.CreateReviewerRequest;
import konkuk.ptal.dto.request.UpdateUserRequest;
import konkuk.ptal.entity.Reviewee;
import konkuk.ptal.entity.Reviewer;
import konkuk.ptal.entity.User;
import konkuk.ptal.exception.BadRequestException;
import konkuk.ptal.exception.DuplicatedEmailException;
import konkuk.ptal.repository.RevieweeRepository;
import konkuk.ptal.repository.ReviewerRepository;
import konkuk.ptal.repository.UserRepository;
import konkuk.ptal.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReviewerRepository reviewerRepository;
    private final RevieweeRepository revieweeRepository;

    @Transactional
    @Override
    public Reviewer registerReviewer(CreateReviewerRequest dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicatedEmailException(ErrorCode.DUPLICATED_EMAIL);
        }
        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        User user = User.createUser(dto.getEmail(), dto.getName(), hashedPassword);
        user.updateRole(Role.REVIEWER);
        User savedUser = userRepository.save(user);
        Reviewer reviewer = Reviewer.createReviewer(savedUser, dto);

        Reviewer savedReviewer = reviewerRepository.save(reviewer);

        return savedReviewer;
    }

    @Transactional(readOnly = true)
    @Override
    public Reviewer getReviewer(Long id) {
        return reviewerRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    @Override
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

    @Transactional
    @Override
    public Reviewee registerReviewee(CreateRevieweeRequest dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicatedEmailException(ErrorCode.DUPLICATED_EMAIL);
        }
        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        User user = User.createUser(dto.getEmail(), dto.getName(), hashedPassword);
        user.updateRole(Role.REVIEWEE);

        User savedUser = userRepository.save(user);

        Reviewee reviewee = Reviewee.createReviewee(savedUser, dto);
        Reviewee savedReviewee = revieweeRepository.save(reviewee);

        return savedReviewee;
    }

    @Override
    @Transactional(readOnly = true)
    public Reviewee getReviewee(Long id) {
        return revieweeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public Reviewee updateReviewee(Long id, CreateRevieweeRequest dto, Long authenticatedUserId) {
        Reviewee reviewee = getReviewee(id);

        // 리뷰어 본인만 수정 가능
        if (!reviewee.getUser().getId().equals(authenticatedUserId)) {
            throw new BadRequestException(ErrorCode.INVALID_JWT);
        }
        reviewee.setPreferences(dto.getPreferences());

        return revieweeRepository.save(reviewee);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public User updateUser(Long id, UpdateUserRequest dto) {
        User user = getUser(id);
        dto.getEmail().ifPresent(user::setEmail);
        dto.getName().ifPresent(user::setName);
        dto.getPassword().ifPresent(newPw -> {
            String hashedPassword = passwordEncoder.encode(newPw);
            user.setPasswordHash(hashedPassword);
        });
        return userRepository.save(user);
    }
}
