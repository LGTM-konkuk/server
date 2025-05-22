package konkuk.ptal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRevieweeDto {

    @NotBlank
    private Long userId;

    @NotBlank(message = "displayName은 비어 있을 수 없습니다.")
    private String displayName;

    @NotEmpty(message = "하나 이상의 preferences를 포함해야합니다.")
    @Size(max = 5, message = "preferences는 최대 5개까지 입력할 수 있습니다.")
    private List<String> preferences;

}
