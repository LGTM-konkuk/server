package konkuk.ptal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateReviewRequest {
    @NotBlank(message = "review content는 비어 있을 수 없습니다.")
    private String reviewContent;
}
