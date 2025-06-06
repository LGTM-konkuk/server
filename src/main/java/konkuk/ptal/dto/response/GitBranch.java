package konkuk.ptal.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GitBranch {
    private String name;
    private Boolean isDefault;
    private String lastCommit;
    private LocalDateTime lastCommitDate;
    private String lastCommitMessage;

    public static GitBranch from(String name, Boolean isDefault, String lastCommit, LocalDateTime lastCommitDate, String lastCommitMessage) {
        return GitBranch.builder()
                .name(name)
                .isDefault(isDefault)
                .lastCommit(lastCommit)
                .lastCommitDate(lastCommitDate)
                .lastCommitMessage(lastCommitMessage)
                .build();
    }
}
