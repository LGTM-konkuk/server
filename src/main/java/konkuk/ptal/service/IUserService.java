package konkuk.ptal.service;


import konkuk.ptal.dto.request.CreateRevieweeRequest;
import konkuk.ptal.dto.request.CreateReviewerRequest;
import konkuk.ptal.entity.Reviewee;
import konkuk.ptal.entity.Reviewer;

public interface IUserService {
    Reviewer registerReviewer(CreateReviewerRequest dto, Long authenticatedUserId);

    Reviewer getReviewer(Long id);

    Reviewer updateReviewer(Long id, CreateReviewerRequest dto, Long authenticatedUserId);

    Reviewee registerReviewee(CreateRevieweeRequest dto, Long authenticatedUserId);

    Reviewee getReviewee(Long id);

    Reviewee updateReviewee(Long id, CreateRevieweeRequest dto, Long authenticatedUserId);
}
