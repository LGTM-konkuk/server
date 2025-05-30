package konkuk.ptal.controller;

import konkuk.ptal.service.IReviewSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/review-submissions")
@RequiredArgsConstructor
public class ReviewSubmissionController {

    private final IReviewSubmissionService reviewRequestService; // TODO: Define and implement

}