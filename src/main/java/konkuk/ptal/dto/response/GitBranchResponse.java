package konkuk.ptal.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GitBranchResponse {
    private String name;
    private Boolean isDefault;
    private String lastCommit;
    private LocalDateTime lastCommitDate;
    private String lastCommitMessage;

    public static GitBranchResponse from(String name, Boolean isDefault, String lastCommit, LocalDateTime lastCommitDate, String lastCommitMessage) {
        return GitBranchResponse.builder()
                .name(name)
                .isDefault(isDefault)
                .lastCommit(lastCommit)
                .lastCommitDate(lastCommitDate)
                .lastCommitMessage(lastCommitMessage)
                .build();
    }
}
