package konkuk.ptal.dto.request;

import lombok.Data;

@Data
public class CreateReviewCommentRequest {
    private String content;
    private Long userId;
    private Long codeFileId;
    private Integer lineNumber;
    private String parentCommentId;
}
