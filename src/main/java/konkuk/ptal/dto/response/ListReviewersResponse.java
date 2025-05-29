package konkuk.ptal.dto.response;

import konkuk.ptal.entity.Reviewer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ListReviewersResponse extends BasePageResponse {
    private List<ReadReviewerResponse> content;


    public ListReviewersResponse(Page<Reviewer> page, List<ReadReviewerResponse> content) {
        super(page);
        this.content = content;
    }

    public static ListReviewersResponse from(Page<Reviewer> reviewerPage) {

        List<ReadReviewerResponse> content = reviewerPage.getContent().stream()
                .map(ReadReviewerResponse::from)
                .collect(Collectors.toList());

        return new ListReviewersResponse(reviewerPage, content);
    }
}
