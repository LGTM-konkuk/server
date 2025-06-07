package konkuk.ptal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReviewRequest {
    private Long reviewSubmissionId;

    @NotBlank(message = "review content는 비어 있을 수 없습니다.")
    private String reviewContent;

}
