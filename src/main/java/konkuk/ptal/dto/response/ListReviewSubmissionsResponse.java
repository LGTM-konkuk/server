package konkuk.ptal.dto.response;

import konkuk.ptal.entity.ReviewRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ListReviewSubmissionsResponse extends BasePageResponse {
    private List<ReadReviewSubmissionResponse> content;


    public ListReviewSubmissionsResponse(Page<ReviewRequest> page, List<ReadReviewSubmissionResponse> content) {
        super(page);
        this.content = content;
    }

    public static ListReviewSubmissionsResponse from(Page<ReviewRequest> reviewRequestPage) {

        List<ReadReviewSubmissionResponse> content = reviewRequestPage.getContent().stream()
                .map(reviewRequest -> ReadReviewSubmissionResponse.from(
                        reviewRequest,
                        reviewRequest.getReviewee(),
                        reviewRequest.getReviewer()
                ))
                .collect(Collectors.toList());

        return new ListReviewSubmissionsResponse(reviewRequestPage, content);
    }
}
