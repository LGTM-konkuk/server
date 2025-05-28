package konkuk.ptal.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UpdateReviewerRequest {
    @NotBlank
    List<String> preferences;

    @NotBlank
    String bio;

    @NotBlank
    List<String> tags;

}
