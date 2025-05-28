package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.CreateReviewSubmissionRequest;
import konkuk.ptal.dto.request.UpdateReviewRequest;
import konkuk.ptal.dto.response.CreateRevieweeResponse;
import konkuk.ptal.dto.response.ReviewResponse;
import konkuk.ptal.dto.response.CreateReviewerResponse;
import konkuk.ptal.service.IReviewService; // TODO: Create this service interface
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService reviewService; // TODO: Define and implement

}