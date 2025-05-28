package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.ReviewCreateRequest;
import konkuk.ptal.dto.request.ReviewUpdateRequest;
import konkuk.ptal.dto.response.RevieweeResponse;
import konkuk.ptal.dto.response.ReviewResponse;
import konkuk.ptal.dto.response.ReviewerResponse;
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

    /**
     * Submit a new review (by a Reviewer).
     * Corresponds to POST /api/v1/reviews in OpenAPI spec.
     * OpenAPI spec now states 200 OK for successful creation.
     *
     * @param createDto The data transfer object containing review details.
     * @param reviewerUserId The ID of the authenticated reviewer user.
     * @return ResponseEntity with ApiResponse containing the created ReviewResponseDto.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @RequestBody ReviewCreateRequest createDto,
            @AuthenticationPrincipal Long reviewerUserId) {
        // TODO: Implement service logic:
        // 1. Validate if the authenticated user (reviewerUserId) is a reviewer.
        // 2. Fetch the ReviewRequest using createDto.getReviewRequestId().
        // 3. Validate if the reviewRequestId exists and is in an 'APPROVED' state.
        // 4. Validate if the authenticated reviewer (reviewerUserId) is the designated reviewer for the request.
        // 5. Check if a review already exists for this request.
        // 6. Save the new review in the database.
        // 7. Optionally, update the review_requests status or create a review_feeds entry.

        // Review review = reviewService.createReview(createDto, reviewerUserId); // TODO: 서비스 호출
        // ReviewResponseDto responseDto = ReviewResponseDto.from(review); // TODO: 엔티티 -> DTO 변환

        System.out.println("Creating review for request ID: " + createDto.getReviewRequestId() + " by reviewer user ID: " + reviewerUserId);

        // Dummy ReviewResponseDto for demonstration
        // userId 필드를 완전히 제거했습니다.
        ReviewResponse dummyResponse = new ReviewResponse(
                1L, // Dummy Review ID (ReviewResponseDto의 id)
                ReviewerResponse.builder()
                        .id(10L) // Dummy Reviewer Profile ID (Reviewer 테이블의 ID)
                        .expertise("Expertise A")
                        .bio("Bio A")
                        .tags(List.of("tag1", "tag2"))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                RevieweeResponse.builder()
                        .id(20L) // Dummy Reviewee Profile ID (Reviewee 테이블의 ID)
                        .displayName("Reviewee Display")
                        .preferences(List.of("pref1"))
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                createDto.getReviewRequestId(),
                createDto.getReviewContent(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        return ResponseEntity.ok(ApiResponse.success(ResponseCode.REVIEW_CREATED, dummyResponse));
    }

    /**
     * List reviews for the authenticated user.
     * Corresponds to GET /api/v1/reviews in OpenAPI spec.
     *
     * @param userId The ID of the authenticated user.
     * @param type   Optional filter for reviews ("written", "received", "all").
     * @return ResponseEntity with ApiResponse containing a list of ReviewResponseDto.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> listMyReviews(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String type // e.g., "written", "received", "all"
    ) {
        // TODO: Implement service logic:
        // 1. Determine if the user is a reviewer or reviewee based on their role/profile.
        // 2. Fetch reviews based on the 'type' parameter and user ID.
        //    - "written": Reviews authored by this user (as a reviewer).
        //    - "received": Reviews received by this user (as a reviewee).
        //    - "all" or null: Both written and received reviews.
        // 3. Map Review entities to ReviewResponseDto.

        // List<Review> reviews = reviewService.listReviewsForUser(userId, type); // TODO: 서비스 호출
        // List<ReviewResponseDto> responseDtos = reviews.stream().map(ReviewResponseDto::from).collect(Collectors.toList()); // TODO: 엔티티 -> DTO 변환

        System.out.println("Listing reviews for user ID: " + userId + " with type: " + type);

        // Dummy list of ReviewResponseDto for demonstration
        // userId 필드를 완전히 제거했습니다.
        List<ReviewResponse> dummyReviews = List.of(
                new ReviewResponse(
                        101L,
                        ReviewerResponse.builder() // Builder 패턴 사용
                                .id(1L)
                                .expertise("Java Expert")
                                .bio("Passionate about code reviews")
                                .tags(List.of("java", "spring"))
                                .createdAt(LocalDateTime.now().minusDays(10))
                                .updatedAt(LocalDateTime.now().minusDays(10))
                                .build(),
                        RevieweeResponse.builder()
                                .id(1L)
                                .displayName("Learning Dev")
                                .preferences(List.of("backend"))
                                .createdAt(LocalDateTime.now().minusDays(10))
                                .updatedAt(LocalDateTime.now().minusDays(10))
                                .build(),
                        1001L,
                        "Excellent code quality and clear structure. Consider adding more unit tests.",
                        LocalDateTime.now().minusDays(5),
                        LocalDateTime.now().minusDays(5)
                ),
                new ReviewResponse(
                        102L,
                        ReviewerResponse.builder() // Builder 패턴 사용
                                .id(2L)
                                .expertise("Frontend Guru")
                                .bio("Loves UI/UX")
                                .tags(List.of("react", "css"))
                                .createdAt(LocalDateTime.now().minusDays(8))
                                .updatedAt(LocalDateTime.now().minusDays(8))
                                .build(),
                        RevieweeResponse.builder()
                                .id(2L)
                                .displayName("Learning Dev")
                                .preferences(List.of("backend"))
                                .createdAt(LocalDateTime.now().minusDays(3))
                                .updatedAt(LocalDateTime.now().minusDays(3))
                                .build(),
                        1002L,
                        "Good component separation, but styling could be more consistent.",
                        LocalDateTime.now().minusDays(3),
                        LocalDateTime.now().minusDays(3)
                )
        );
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.DATA_RETRIEVED, dummyReviews));
    }

    /**
     * Get a specific review by its ID.
     * Corresponds to GET /api/v1/reviews/{reviewId} in OpenAPI spec.
     *
     * @param reviewId The ID of the review to retrieve.
     * @param userId The ID of the authenticated user (for access control).
     * @return ResponseEntity with ApiResponse containing the ReviewResponseDto.
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal Long userId) {
        // TODO: Implement service logic:
        // 1. Fetch the review by reviewId.
        // 2. Validate if the authenticated user (userId) is either the reviewer or the reviewee of this review.

        // Review review = reviewService.getReviewById(reviewId, userId); // TODO: 서비스 호출
        // ReviewResponseDto responseDto = ReviewResponseDto.from(review); // TODO: 엔티티 -> DTO 변환

        System.out.println("Getting review ID: " + reviewId + " for user ID: " + userId);

        // Dummy ReviewResponseDto for demonstration
        // userId 필드를 완전히 제거했습니다.
        ReviewResponse dummyResponse = new ReviewResponse(
                reviewId,
                ReviewerResponse.builder()
                        .id(1L)
                        .expertise("Java Expert")
                        .bio("Passionate about code reviews")
                        .tags(List.of("java", "spring"))
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .updatedAt(LocalDateTime.now().minusDays(10))
                        .build(),
                RevieweeResponse.builder()
                        .id(1L)
                        .displayName("Reviewee Alice")
                        .preferences(List.of("backend"))
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .updatedAt(LocalDateTime.now().minusDays(10))
                        .build(),
                1001L, // Dummy Review Request ID
                "This is the content for review ID: " + reviewId + ". It's a very detailed review.",
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now().minusDays(7)
        );
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.DATA_RETRIEVED, dummyResponse));
    }

    /**
     * Update an existing review. Only the original reviewer can update their review.
     * Corresponds to PUT /api/v1/reviews/{reviewId} in OpenAPI spec.
     *
     * @param reviewId The ID of the review to update.
     * @param updateDto The data transfer object containing updated review content.
     * @param reviewerUserId The ID of the authenticated reviewer user.
     * @return ResponseEntity with ApiResponse containing the updated ReviewResponseDto.
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest updateDto,
            @AuthenticationPrincipal Long reviewerUserId) {
        // TODO: Implement service logic:
        // 1. Fetch the review by reviewId.
        // 2. Validate if the authenticated user (reviewerUserId) is the original author of this review.
        // 3. Optionally, check if the update window has passed (e.g., reviews can only be updated within 24 hours).
        // 4. Update the review content in the database.

        // Review updatedReview = reviewService.updateReview(reviewId, updateDto, reviewerUserId); // TODO: 서비스 호출
        // ReviewResponseDto responseDto = ReviewResponseDto.from(updatedReview); // TODO: 엔티티 -> DTO 변환

        System.out.println("Updating review ID: " + reviewId + " by reviewer user ID: " + reviewerUserId + " with content: " + updateDto.getReviewContent());

        // Dummy ReviewResponseDto for demonstration, reflecting the update
        // userId 필드를 완전히 제거했습니다.
        ReviewResponse dummyUpdatedResponse = new ReviewResponse(
                reviewId,
                ReviewerResponse.builder()
                        .id(1L)
                        .expertise("Java Expert")
                        .bio("Passionate about code reviews")
                        .tags(List.of("java", "spring"))
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .updatedAt(LocalDateTime.now())
                        .build(),
                RevieweeResponse.builder()
                        .id(1L)
                        .displayName("Reviewee Alice")
                        .preferences(List.of("backend"))
                        .createdAt(LocalDateTime.now().minusDays(10))
                        .updatedAt(LocalDateTime.now().minusDays(10))
                        .build(),
                1001L, // Dummy Review Request ID
                updateDto.getReviewContent(), // Updated content
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now() // Updated timestamp for the review
        );
        return ResponseEntity.ok(ApiResponse.success(ResponseCode.REVIEW_UPDATED, dummyUpdatedResponse));
    }
}