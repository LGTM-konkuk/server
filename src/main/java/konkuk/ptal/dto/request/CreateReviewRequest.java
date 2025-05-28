package konkuk.ptal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateReviewRequest {
    @NotNull
    Long reviewSubmissionId;
    @Size(min = 10, max = 5000)
    String reviewContent;
}
