package konkuk.ptal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewSubmissionRequest {
    private Long reviewerId;

    @Pattern(regexp = "^https?://.*$", message = "올바른 HTTP/HTTPS URL 형식이어야 합니다")
    private String gitUrl;

    @NotBlank(message = "브랜치명은 필수입니다")
    private String branch;

    @NotBlank(message = "리뷰 요청 상세 내용은 필수입니다")
    private String requestDetails;
}
