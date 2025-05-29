package konkuk.ptal.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import konkuk.ptal.dto.response.UserMinimalResponse;
import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
public class UpdateUserRequest {

    @Email(message = "유효한 이메일 주소여야 합니다.")
    private Optional<String> email;

    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private Optional<String> password;

    private Optional<String> name;
}