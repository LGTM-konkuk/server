package konkuk.ptal.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserRequest {

    @Email(message = "유효한 이메일 주소여야 합니다.")
    private String email;

    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    private String name;

    // Optional getter 메서드들
    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }
}