package konkuk.ptal.service;


import konkuk.ptal.dto.request.CreateRevieweeDto;
import konkuk.ptal.dto.request.CreateReviewerRequestDto;
import konkuk.ptal.entity.Reviewee;
import konkuk.ptal.entity.Reviewer;

public interface IUserService {
    Reviewer registerReviewer(CreateReviewerRequestDto dto, Long authenticatedUserId);

    Reviewer getReviewer(Long id);

    Reviewer updateReviewer(Long id, CreateReviewerRequestDto dto, Long authenticatedUserId);

    Reviewee registerReviewee(CreateRevieweeDto dto, Long authenticatedUserId);

    Reviewee getReviewee(Long id);

    Reviewee updateReviewee(Long id, CreateRevieweeDto dto, Long authenticatedUserId);
}
