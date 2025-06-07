package konkuk.ptal.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import konkuk.ptal.domain.UserPrincipal;
import konkuk.ptal.domain.enums.ReviewSubmissionType;
import konkuk.ptal.dto.api.ApiResponse;
import konkuk.ptal.dto.api.ErrorCode;
import konkuk.ptal.dto.api.ResponseCode;
import konkuk.ptal.dto.request.CreateReviewSubmissionRequest;
import konkuk.ptal.dto.response.FileContentResponse;
import konkuk.ptal.dto.response.ListBranchesResponse;
import konkuk.ptal.dto.response.ListReviewSubmissionResponse;
import konkuk.ptal.dto.response.ProjectFileSystemResponse;
import konkuk.ptal.dto.response.ReadReviewSubmissionResponse;
import konkuk.ptal.entity.ReviewSubmission;
import konkuk.ptal.service.IFileService;
import konkuk.ptal.service.IReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ReviewSubmissionController {
    private final IReviewService reviewService;
    private final IFileService fileService;

    @PostMapping("/review-submissions/new")
    public ResponseEntity<ApiResponse<ReadReviewSubmissionResponse>> createReviewSubmission(
            @Valid @RequestBody CreateReviewSubmissionRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ReviewSubmission reviewSubmission = reviewService.createReviewSubmission(request, userPrincipal);
        ProjectFileSystemResponse fileSystem = fileService.getProjectFileSystem(reviewSubmission.getGitUrl(), reviewSubmission.getBranch(), reviewSubmission.getId());

        ReadReviewSubmissionResponse responseDto = ReadReviewSubmissionResponse.from(reviewSubmission, fileSystem);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(ResponseCode.REVIEW_SUBMISSION_CREATED.getMessage(), responseDto));
    }

    @GetMapping("/review-submissions")
    public ResponseEntity<ApiResponse<ListReviewSubmissionResponse>> getReviewSubmissions(
            @RequestParam(name = "type", required = false, defaultValue = "ALL") ReviewSubmissionType type,
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
        ProjectFileSystemResponse fileSystem = fileService.getProjectFileSystem(reviewSubmission.getGitUrl(), reviewSubmission.getBranch(), reviewSubmission.getId());
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
        ProjectFileSystemResponse fileSystem = fileService.getProjectFileSystem(canceledReviewSubmission.getGitUrl(), canceledReviewSubmission.getBranch(), canceledReviewSubmission.getId());
        ReadReviewSubmissionResponse responseDto = ReadReviewSubmissionResponse.from(canceledReviewSubmission, fileSystem);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.REVIEW_SUBMISSION_CANCELED.getMessage(), responseDto));
    }

    @GetMapping("/review-submissions/{submissionId}/filesystem")
    public ResponseEntity<ApiResponse<ProjectFileSystemResponse>> getProjectFileSystem(
            @PathVariable("submissionId") Long submissionId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        ReviewSubmission reviewSubmission = reviewService.getReviewSubmission(submissionId, userPrincipal);

        ProjectFileSystemResponse fileSystem = fileService.getProjectFileSystem(
                reviewSubmission.getGitUrl(),
                reviewSubmission.getBranch(),
                reviewSubmission.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.DATA_RETRIEVED.getMessage(), fileSystem));
    }

    @GetMapping("/review-submissions/{submissionId}/files")
    public ResponseEntity<ApiResponse<FileContentResponse>> getReviewSubmissionSpecificFile(
            @PathVariable("submissionId") Long submissionId,
            @RequestParam(value = "path", required = false) String filePath,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        String requestedPath = (filePath == null || filePath.isEmpty()) ? "" : filePath;

        if (requestedPath.contains("..") || requestedPath.startsWith("/") || requestedPath.contains("\0")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.fail(ErrorCode.NOT_PERMITTED_FILE_PATH.getMessage(), null));
        }
        ReviewSubmission reviewSubmission = reviewService.getReviewSubmission(submissionId, userPrincipal);

        FileContentResponse fileContentResponse = fileService.getFileContent(
                reviewSubmission.getGitUrl(),
                reviewSubmission.getBranch(),
                requestedPath);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.DATA_RETRIEVED.getMessage(), fileContentResponse));
    }

    @GetMapping("/git/branches")
    public ResponseEntity<ApiResponse<ListBranchesResponse>> getBranches(
            @RequestParam(name = "gitUrl") String gitUrl,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        ListBranchesResponse responseDtos = fileService.getBranches(gitUrl);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(ResponseCode.DATA_RETRIEVED.getMessage(), responseDtos));
    }

}

