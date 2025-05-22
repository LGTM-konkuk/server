package konkuk.ptal.controller;

import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ResponseCode;
// TODO: Define this DTO, it might be similar to ReviewResponseDto or a specialized FeedItemDto
// import konkuk.ptal.dto.response.ReviewFeedItemDto;
import konkuk.ptal.service.IReviewFeedService; // TODO: Create this service interface
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feed") // Or "/api/review-feed"
@RequiredArgsConstructor
public class ReviewFeedController {

    private final IReviewFeedService reviewFeedService; // TODO: Define and implement

    // Get the personalized review feed for the authenticated user
    @GetMapping
    public ResponseEntity<ApiResponse<List</*TODO: ReviewFeedItemDto*/Object>>> getMyFeed(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Page<ReviewFeedItemDto> feedItems = reviewFeedService.getUserFeed(userId, PageRequest.of(page, size));
        // return ResponseEntity.ok(ApiResponse.success(ResponseCode.DATA_RETRIEVED, feedItems.getContent()));
        // TODO: Implement service, DTO, and pagination
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.DATA_RETRIEVED, List.of("Feed items for user ID: " + userId + " - (TODO: Implement)")));
    }
}