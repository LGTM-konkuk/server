package konkuk.ptal.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FileContent {
    private String path;
    private String content;
    private String encoding;
    private Long size;
    private LocalDateTime lastModified;
    private Integer lineCount;

    public static FileContent from(String path, String content, String encoding, Long size, LocalDateTime lastModified, Integer lineCount){
        return FileContent.builder()
                .path(path)
                .content(content)
                .encoding(encoding)
                .size(size)
                .lastModified(lastModified)
                .lineCount(lineCount)
                .build();
    }
}
