package konkuk.ptal.controller;

import jakarta.validation.Valid;
import konkuk.ptal.domain.UserPrincipal;
import konkuk.ptal.domain.enums.ReviewSubmissionType;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.CreateReviewSubmissionRequest;
import konkuk.ptal.dto.response.FileContent;
import konkuk.ptal.dto.response.ListReviewSubmissionResponse;
import konkuk.ptal.dto.response.ProjectFileSystem;
import konkuk.ptal.dto.response.ReadReviewSubmissionResponse;
import konkuk.ptal.entity.ReviewSubmission;
import konkuk.ptal.service.IFileService;
import konkuk.ptal.service.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewSubmissionController {
    private final IReviewService reviewService;
    private final IFileService fileService;

    @PostMapping("/review-submissions/new")
    public ResponseEntity<ApiResponse<ReadReviewSubmissionResponse>> createReviewSubmission(
            @Valid @RequestBody CreateReviewSubmissionRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ReviewSubmission reviewSubmission = reviewService.createReviewSubmission(request, userPrincipal);
        ProjectFileSystem fileSystem = fileService.getProjectFileSystem(reviewSubmission.getGitUrl(), reviewSubmission.getBranch(), reviewSubmission.getId());

        ReadReviewSubmissionResponse responseDto = ReadReviewSubmissionResponse.from(reviewSubmission, fileSystem);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(ResponseCode.REVIEW_SUBMISSION_CREATED.getMessage(), responseDto));
    }

    @GetMapping("/review-submissions")
    public ResponseEntity<ApiResponse<ListReviewSubmissionResponse>> getReviewSubmissions(
            @RequestParam(name = "type", required = false, defaultValue = "all") ReviewSubmissionType type,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ListReviewSubmissionResponse responseDtos = reviewService.getReviewSubmissions(type, page, size, userPrincipal);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.DATA_RETRIEVED.getMessage(), responseDtos));
    }

    @GetMapping("/review-submissions/{submissionId}")
    public ResponseEntity<ApiResponse<ReadReviewSubmissionResponse>> getReviewSubmissionById(
            @PathVariable("submissionId") Long submissionId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ReviewSubmission reviewSubmission = reviewService.getReviewSubmission(submissionId, userPrincipal);
        ProjectFileSystem fileSystem = fileService.getProjectFileSystem(reviewSubmission.getGitUrl(), reviewSubmission.getBranch(), reviewSubmission.getId());
        ReadReviewSubmissionResponse responseDto = ReadReviewSubmissionResponse.from(reviewSubmission, fileSystem);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.DATA_RETRIEVED.getMessage(), responseDto));
    }

    @PatchMapping("/review-submissions/{submissionId}")
    public ResponseEntity<ApiResponse<ReadReviewSubmissionResponse>> cancelReviewSubmission(
            @PathVariable("submissionId") Long submissionId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ReviewSubmission canceledReviewSubmission = reviewService.cancelReviewSubmission(submissionId, userPrincipal);
        ProjectFileSystem fileSystem = fileService.getProjectFileSystem(canceledReviewSubmission.getGitUrl(), canceledReviewSubmission.getBranch(), canceledReviewSubmission.getId());
        ReadReviewSubmissionResponse responseDto = ReadReviewSubmissionResponse.from(canceledReviewSubmission, fileSystem);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.REVIEW_SUBMISSION_CANCELED.getMessage(), responseDto));
    }

    @GetMapping("/{submissionId}/filesystem")
    public ResponseEntity<ApiResponse<ProjectFileSystem>> getProjectFileSystem(
            @PathVariable("submissionId") Long submissionId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        ReviewSubmission reviewSubmission = reviewService.getReviewSubmission(submissionId, userPrincipal);

        ProjectFileSystem fileSystem = fileService.getProjectFileSystem(
                reviewSubmission.getGitUrl(),
                reviewSubmission.getBranch(),
                reviewSubmission.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.DATA_RETRIEVED.getMessage(), fileSystem));
    }

    @GetMapping("/{submissionId}/files/{filePath}")
    public ResponseEntity<ApiResponse<FileContent>> getReviewSubmissionSpecificFile(
            @PathVariable("submissionId") Long submissionId,
            @PathVariable("filePath") String filePath,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        ReviewSubmission reviewSubmission = reviewService.getReviewSubmission(submissionId, userPrincipal);

        String decodedFilePath;
        try {
            decodedFilePath = java.net.URLDecoder.decode(filePath, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            decodedFilePath = filePath;
        }

        FileContent fileContent = fileService.getFileContent(
                reviewSubmission.getGitUrl(),
                reviewSubmission.getBranch(),
                decodedFilePath);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.DATA_RETRIEVED.getMessage(), fileContent));
    }

}

