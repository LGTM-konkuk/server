package konkuk.ptal.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
