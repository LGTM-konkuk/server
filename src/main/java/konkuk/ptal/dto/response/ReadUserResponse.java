package konkuk.ptal.dto.response;

import konkuk.ptal.domain.enums.Role;
import konkuk.ptal.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReadUserResponse {
    Long id;
    String email;
    String name;
    Role role;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static ReadUserResponse from(User user) {
        BaseAuditResponse baseAuditResponse = BaseAuditResponse.from(user.getCreatedAt());
        return ReadUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .createdAt(baseAuditResponse.getCreatedAt())
                .updatedAt(baseAuditResponse.getUpdatedAt())
                .build();
    }
}
