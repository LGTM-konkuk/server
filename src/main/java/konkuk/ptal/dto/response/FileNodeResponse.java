package konkuk.ptal.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import konkuk.ptal.domain.enums.FileNodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileNodeResponse {
    private String name;
    private String path;
    private FileNodeType type;
    private Long size; // 디렉토리인 경우 null
    private LocalDateTime lastModified;
    private List<FileNodeResponse> children; // 파일인 경우 null

    public static FileNodeResponse from(String entryName, String entryPath, FileNodeType fileNodeType, Long fileSize, LocalDateTime lastModified, List<FileNodeResponse> children) {
        return FileNodeResponse.builder()
                .name(entryName)
                .path(entryPath)
                .type(fileNodeType)
                .size(fileSize)
                .lastModified(lastModified)
                .children(children)
                .build();
    }
}
