package konkuk.ptal.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReadCommentsOfReviewResponse {
    private Integer totalComments;
    private List<ReadCommentResponse> content;
}
