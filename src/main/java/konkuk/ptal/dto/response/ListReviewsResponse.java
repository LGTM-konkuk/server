package konkuk.ptal.dto.response;

import konkuk.ptal.entity.Review;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ListReviewsResponse extends BasePageResponse {
    private List<ReadReviewResponse> content;


    public ListReviewsResponse(Page<Review> page, List<ReadReviewResponse> content) {
        super(page);
        this.content = content;
    }

    public static ListReviewsResponse from(Page<Review> reviewPage) {

        List<ReadReviewResponse> content = reviewPage.getContent().stream()
                .map(review -> ReadReviewResponse.from(
                        review,
                        review.getReviewee(),
                        review.getReviewer()
                ))
                .collect(Collectors.toList());

        return new ListReviewsResponse(reviewPage, content);
    }
}
