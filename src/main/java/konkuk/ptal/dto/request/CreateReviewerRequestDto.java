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
public class CreateReviewerRequestDto {

    @NotBlank
    private Integer userId;

    @NotBlank(message = "expertise는 비어 있을 수 없습니다.")
    private String expertise;

    @NotBlank(message = "bio는 비어 있을 수 없습니다.")
    @Size(max = 1000, message = "bio는 최대 1000자까지 입력할 수 있습니다.")
    private String bio;

    @NotEmpty(message = "tags는 최소 하나 이상의 태그를 포함해야 합니다.")
    @Size(max = 5, message = "tags는 최대 5개까지 입력할 수 있습니다.")
    private List<@NotBlank(message = "태그는 비어 있을 수 없습니다.") String> tags;
}
