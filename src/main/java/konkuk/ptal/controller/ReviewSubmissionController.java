package konkuk.ptal.controller;

import konkuk.ptal.service.IReviewRequestService; // TODO: Create this service interface
import lombok.RequiredArgsConstructor;
        import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/review-submissions")
@RequiredArgsConstructor
public class ReviewSubmissionController {

    private final IReviewRequestService reviewRequestService; // TODO: Define and implement

}