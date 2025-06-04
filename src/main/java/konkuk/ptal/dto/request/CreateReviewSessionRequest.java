package konkuk.ptal.dto.request;

import lombok.Data;

@Data
public class CreateReviewSessionRequest {
    private String title;
    private String description;
    private Long revieweeId;
    private Long reviewerId;
    private String githubLink;
    private String branchName;
}
