package konkuk.ptal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRevieweeRequest {
    @NotBlank
    List<String> preferences;

}
