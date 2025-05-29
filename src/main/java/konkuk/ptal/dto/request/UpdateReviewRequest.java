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
public class UpdateReviewRequest {
    @NotNull
    @Size(min = 10, max = 5000) // 예시: 최소 10자, 최대 5000자
    private String reviewContent;
}
