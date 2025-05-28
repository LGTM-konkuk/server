package konkuk.ptal.dto.request;

import jakarta.validation.constraints.NotBlank;
import konkuk.ptal.dto.response.UserMinimalResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserRequest {
    @NotBlank
    String name;
}
