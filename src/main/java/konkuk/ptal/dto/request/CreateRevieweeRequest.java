package konkuk.ptal.dto.request;

import jakarta.validation.constraints.Email;
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
public class CreateRevieweeRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    @NotBlank
    private String name;

    @NotEmpty(message = "하나 이상의 preferences를 포함해야합니다.")
    @Size(max = 5, message = "preferences는 최대 5개까지 입력할 수 있습니다.")
    private List<String> preferences;

}
