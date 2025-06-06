package konkuk.ptal.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectFileSystemResponse {
    Long submissionId;
    String branch;
    FileNodeResponse rootDirectory;
    Long totalFiles;
    Long totalSize;

    public static ProjectFileSystemResponse from(Long submissionId, String branch, FileNodeResponse rootDirectory, Long totalFiles, Long totalSize) {
        return ProjectFileSystemResponse.builder()
                .submissionId(submissionId)
                .branch(branch)
                .rootDirectory(rootDirectory)
                .totalFiles(totalFiles)
                .totalSize(totalSize)
                .build();
    }
}
