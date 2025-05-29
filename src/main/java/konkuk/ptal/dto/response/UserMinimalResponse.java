package konkuk.ptal.dto.response;

import konkuk.ptal.entity.Reviewee;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
@Builder
public class UserMinimalResponse {
    Long id;
    String name;
    String email;

    public static UserMinimalResponse from(Long id,String name, String email) {
        return UserMinimalResponse.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();
    }
}
