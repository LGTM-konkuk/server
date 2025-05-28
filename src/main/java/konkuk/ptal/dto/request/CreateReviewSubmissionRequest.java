package konkuk.ptal.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewSubmissionRequest {
    @NotNull
    private Long reviewerId;

    @NotNull
    private String gitUrl;

    @NotNull
    private String requestDetails;
}