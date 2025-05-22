package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ResponseCode;
// TODO: Define these DTOs
// import konkuk.ptal.dto.request.ReviewRequestCreateDto;
// import konkuk.ptal.dto.request.ReviewRequestUpdateDto; // If direct update is needed beyond status change
// import konkuk.ptal.dto.response.ReviewRequestResponseDto;
import konkuk.ptal.service.IReviewRequestService; // TODO: Create this service interface
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List; // For list responses

@RestController
@RequestMapping("/api/review-requests")
@RequiredArgsConstructor
public class ReviewRequestController {

    private final IReviewRequestService reviewRequestService; // TODO: Define and implement

    // Create a new review request (by a Reviewee)
    @PostMapping
    public ResponseEntity<ApiResponse</*TODO: ReviewRequestResponseDto*/Object>> createReviewRequest(
            @Valid @RequestBody /*TODO: ReviewRequestCreateDto*/ Object createDto,
            @AuthenticationPrincipal Long revieweeUserId) { // Authenticated user is the reviewee
        // ReviewRequest reviewRequest = reviewRequestService.createRequest(createDto, revieweeUserId);
        // ReviewRequestResponseDto responseDto = ReviewRequestResponseDto.from(reviewRequest);
        // return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ResponseCode.REVIEW_REQUEST_CREATED, responseDto));
        // TODO: Implement service and DTO
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, "Review request created by user ID: " + revieweeUserId + " - (TODO: Implement)"));
    }

    // Get a specific review request (accessible by involved Reviewer or Reviewee)
    @GetMapping("/{requestId}")
    public ResponseEntity<ApiResponse</*TODO: ReviewRequestResponseDto*/Object>> getReviewRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal Long userId) {
        // ReviewRequest reviewRequest = reviewRequestService.getRequestById(requestId, userId);
        // ReviewRequestResponseDto responseDto = ReviewRequestResponseDto.from(reviewRequest);
        // return ResponseEntity.ok(ApiResponse.success(ResponseCode.DATA_RETRIEVED, responseDto));
        // TODO: Implement service and DTO
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.DATA_RETRIEVED, "Review request ID: " + requestId + " - (TODO: Implement)"));
    }

    // List review requests for the authenticated user (e.g., as Reviewer or Reviewee)
    @GetMapping
    public ResponseEntity<ApiResponse<List</*TODO: ReviewRequestResponseDto*/Object>>> listMyReviewRequests(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String type // e.g., "sent" or "received" or "all"
    ) {
        // List<ReviewRequest> requests = reviewRequestService.listRequestsForUser(userId, type);
        // List<ReviewRequestResponseDto> responseDtos = requests.stream().map(ReviewRequestResponseDto::from).collect(Collectors.toList());
        // return ResponseEntity.ok(ApiResponse.success(ResponseCode.DATA_RETRIEVED, responseDtos));
        // TODO: Implement service and DTO
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.DATA_RETRIEVED, List.of("List of review requests for user ID: " + userId + " - (TODO: Implement)")));
    }

    // Reviewer approves a review request
    @PatchMapping("/{requestId}/approve")
    public ResponseEntity<ApiResponse</*TODO: ReviewRequestResponseDto*/Object>> approveReviewRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal Long reviewerUserId) { // Authenticated user is the reviewer
        // ReviewRequest reviewRequest = reviewRequestService.approveRequest(requestId, reviewerUserId);
        // ReviewRequestResponseDto responseDto = ReviewRequestResponseDto.from(reviewRequest);
        // return ResponseEntity.ok(ApiResponse.success(ResponseCode.REVIEW_REQUEST_APPROVED, responseDto));
        // TODO: Implement service and DTO
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, "Review request ID: " + requestId + " approved by user ID: " + reviewerUserId + " - (TODO: Implement)"));
    }

    // Reviewer rejects a review request
    @PatchMapping("/{requestId}/reject")
    public ResponseEntity<ApiResponse</*TODO: ReviewRequestResponseDto*/Object>> rejectReviewRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal Long reviewerUserId) { // Authenticated user is the reviewer
        // ReviewRequest reviewRequest = reviewRequestService.rejectRequest(requestId, reviewerUserId);
        // ReviewRequestResponseDto responseDto = ReviewRequestResponseDto.from(reviewRequest);
        // return ResponseEntity.ok(ApiResponse.success(ResponseCode.REVIEW_REQUEST_REJECTED, responseDto));
        // TODO: Implement service and DTO
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, "Review request ID: " + requestId + " rejected by user ID: " + reviewerUserId + " - (TODO: Implement)"));
    }

    // Reviewee cancels their own review request (if still PENDING)
    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ApiResponse</*TODO: ReviewRequestResponseDto*/Object>> cancelReviewRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal Long revieweeUserId) { // Authenticated user is the reviewee
        // ReviewRequest reviewRequest = reviewRequestService.cancelRequest(requestId, revieweeUserId);
        // ReviewRequestResponseDto responseDto = ReviewRequestResponseDto.from(reviewRequest);
        // return ResponseEntity.ok(ApiResponse.success(ResponseCode.REVIEW_REQUEST_CANCELED, responseDto));
        // TODO: Implement service and DTO
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.OK, "Review request ID: " + requestId + " canceled by user ID: " + revieweeUserId + " - (TODO: Implement)"));
    }
}