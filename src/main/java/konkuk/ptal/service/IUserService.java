package konkuk.ptal.service;


import konkuk.ptal.dto.request.*;
import konkuk.ptal.dto.response.ListReviewersResponse;
import konkuk.ptal.entity.Reviewee;
import konkuk.ptal.entity.Reviewer;
import konkuk.ptal.entity.User;

import java.util.List;

public interface IUserService {
    Reviewer registerReviewer(CreateReviewerRequest dto);

    Reviewer getReviewer(Long id);

    Reviewer updateReviewer(Long id, UpdateReviewerRequest dto, Long authenticatedUserId);

    Reviewee registerReviewee(CreateRevieweeRequest dto);

    Reviewee getReviewee(Long id);

    Reviewee updateReviewee(Long id, UpdateRevieweeRequest dto, Long authenticatedUserId);

    User getUser(Long id);

    User updateUser(Long id, UpdateUserRequest dto);

    ListReviewersResponse getReviewers(List<String> preferences, List<String> tags, int page, int size);

}
