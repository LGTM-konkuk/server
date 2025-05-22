package konkuk.ptal.service;


import konkuk.ptal.dto.request.CreateReviewerRequestDto;
import konkuk.ptal.entity.Reviewer;

public interface UserService {
    public Reviewer registerReviewer(CreateReviewerRequestDto dto, Long authenticatedUserId);

    public Reviewer getReviewer(Long id);

    public Reviewer updateReviewer(Long id, CreateReviewerRequestDto dto, Long authenticatedUserId);

}
