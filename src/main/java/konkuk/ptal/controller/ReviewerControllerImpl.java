package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.dto.request.CreateReviewerRequestDto;
import konkuk.ptal.dto.response.ReviewerResponseDto;
import konkuk.ptal.entity.Reviewer;
import konkuk.ptal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviewer")
@RequiredArgsConstructor
public class ReviewerControllerImpl implements ReviewerController {

    private final UserService userService;

    @Override
    @PostMapping
    public ResponseEntity<Map<String, ReviewerResponseDto>> registerReviewer(
            @Valid @RequestBody CreateReviewerRequestDto requestDto,
            @AuthenticationPrincipal Long userId) {
        
        Reviewer reviewer = userService.registerReviewer(requestDto, userId);
        ReviewerResponseDto responseDto = ReviewerResponseDto.from(reviewer);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("reviewer", responseDto));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ReviewerResponseDto> getReviewer(@PathVariable Long id) {
        Reviewer reviewer = userService.getReviewer(id);
        return ResponseEntity.ok(ReviewerResponseDto.from(reviewer));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ReviewerResponseDto> updateReviewer(
            @PathVariable Long id,
            @Valid @RequestBody CreateReviewerRequestDto requestDto,
            @AuthenticationPrincipal Long userId) {
        
        Reviewer updatedReviewer = userService.updateReviewer(id, requestDto, userId);
        return ResponseEntity.ok(ReviewerResponseDto.from(updatedReviewer));
    }
} 