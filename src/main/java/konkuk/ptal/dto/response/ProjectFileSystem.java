package konkuk.ptal.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectFileSystem {
    Long submissionId;
    String branch;
    FileNode rootDirectory;
    Long totalFiles;
    Long totalSize;

    public static ProjectFileSystem from(Long submissionId, String branch, FileNode rootDirectory, Long totalFiles, Long totalSize){
        return ProjectFileSystem.builder()
                .submissionId(submissionId)
                .branch(branch)
                .rootDirectory(rootDirectory)
                .totalFiles(totalFiles)
                .totalSize(totalSize)
                .build();
    }
}
