package konkuk.ptal.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
public class BasePageResponse {
    private int totalPages;
    private long totalElements;
    private int page;
    private int size;
    private boolean first;
    private boolean last;
    private int numberOfElements;

    public BasePageResponse(Page<?> page) {
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.numberOfElements = page.getNumberOfElements();
    }
}
