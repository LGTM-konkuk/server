package konkuk.ptal.service;


import konkuk.ptal.dto.request.CreateRevieweeRequest;
import konkuk.ptal.dto.request.CreateReviewerRequest;
import konkuk.ptal.dto.request.UpdateUserRequest;
import konkuk.ptal.entity.Reviewee;
import konkuk.ptal.entity.Reviewer;
import konkuk.ptal.entity.User;

public interface IUserService {
    Reviewer registerReviewer(CreateReviewerRequest dto);

    Reviewer getReviewer(Long id);

    Reviewer updateReviewer(Long id, CreateReviewerRequest dto, Long authenticatedUserId);

    Reviewee registerReviewee(CreateRevieweeRequest dto);

    Reviewee getReviewee(Long id);

    Reviewee updateReviewee(Long id, CreateRevieweeRequest dto, Long authenticatedUserId);

    User getUser(Long id);

    User updateUser(Long id, UpdateUserRequest dto);

}
