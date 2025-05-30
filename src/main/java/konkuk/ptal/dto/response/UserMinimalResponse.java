package konkuk.ptal.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserMinimalResponse {
    Long id;
    String name;
    String email;

    public static UserMinimalResponse from(Long id, String name, String email) {
        return UserMinimalResponse.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }
}
