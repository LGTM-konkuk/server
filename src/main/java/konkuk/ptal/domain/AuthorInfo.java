package konkuk.ptal.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorInfo {
    long id;
    String name;
}
