package konkuk.ptal.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListBranchesResponse {
    private String gitUrl;
    private List<GitBranchResponse> branches;
    private String defaultBranch;

    public static ListBranchesResponse from(String gitUrl, List<GitBranchResponse> branches, String defaultBranch) {
        return ListBranchesResponse.builder()
                .gitUrl(gitUrl)
                .branches(branches)
                .defaultBranch(defaultBranch)
                .build();
    }
}
