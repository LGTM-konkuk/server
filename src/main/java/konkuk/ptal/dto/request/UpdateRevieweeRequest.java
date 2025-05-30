package konkuk.ptal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRevieweeRequest {
    @NotEmpty(message = "preferences는 비어 있을 수 없습니다.")
    List<String> preferences;

}
