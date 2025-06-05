package konkuk.ptal.dto.response;

import lombok.Data;

@Data
public class ProjectFileSystem {
    Long submissionId;
    String branch;
    //FileNode rootDirectory;
    Long totalFiles;
    Long totalSize;
}
