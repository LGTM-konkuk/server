package konkuk.ptal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateReviewerRequest {
    @NotEmpty(message = "preferences는 비어 있을 수 없습니다.")
    List<String> preferences;

    @NotBlank(message = "bio는 비어 있을 수 없습니다.")
    String bio;

    @NotEmpty(message = "tags는 비어 있을 수 없습니다.")
    List<String> tags;

}
