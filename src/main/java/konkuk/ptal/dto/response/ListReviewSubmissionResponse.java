package konkuk.ptal.dto.response;

import konkuk.ptal.entity.ReviewSubmission;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ListReviewSubmissionResponse extends BasePageResponse{
    private List<ReadReviewSubmissionResponse> content;
    public ListReviewSubmissionResponse(Page<ReviewSubmission> page, List<ReadReviewSubmissionResponse> content) {
        super(page);
        this.content = content;
    }

    public static ListReviewSubmissionResponse from(Page<ReviewSubmission> reviewSubmissionPage) {

        List<ReadReviewSubmissionResponse> content = reviewSubmissionPage.getContent().stream()
                .map(ReadReviewSubmissionResponse::from)
                .collect(Collectors.toList());

        return new ListReviewSubmissionResponse(reviewSubmissionPage, content);
    }
}
