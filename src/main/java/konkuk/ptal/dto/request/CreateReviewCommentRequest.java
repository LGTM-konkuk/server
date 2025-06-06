package konkuk.ptal.dto.request;

import lombok.Data;

@Data
public class CreateReviewCommentRequest {
    private String content;
    private String filePath;
    private Integer lineNumber;
    private String parentCommentId;
}
