package konkuk.ptal.service;

import konkuk.ptal.dto.response.FileContent;
import konkuk.ptal.dto.response.ListBranchesResponse;
import konkuk.ptal.dto.response.ProjectFileSystem;
import konkuk.ptal.entity.ReviewSubmission;

import java.util.List;

public interface IFileService {
    /**
     * 특정 Git URL의 브랜치 목록을 조회합니다.
     * @param gitUrl 깃허브 저장소 주소
     * @return 브랜치 목록 정보를 담은 응답 DTO
     */
    ListBranchesResponse getBranches(String gitUrl);

    /**
     * 특정 Git 저장소와 브랜치의 파일 시스템 구조(트리 형태)를 조회합니다.
     * @param gitUrl 깃허브 저장소 주소
     * @param branch 대상 브랜치 이름
     * @param submissionId (옵션) ReviewSubmission ID. ProjectFileSystem DTO에 포함될 정보.
     * @return 프로젝트 파일 시스템 구조를 담은 응답 DTO
     */
    ProjectFileSystem getProjectFileSystem(String gitUrl, String branch, Long submissionId);

    /**
     * 특정 Git 저장소와 브랜치 내 특정 파일의 내용을 조회합니다.
     * @param gitUrl 깃허브 저장소 주소
     * @param branch 대상 브랜치 이름
     * @param filePath 조회할 파일의 저장소 내 상대 경로
     * @return 파일 내용을 담은 응답 DTO
     */
    FileContent getFileContent(String gitUrl, String branch, String filePath);

    /**
     * ReviewSubmission 생성 시, 해당 Git 저장소의 모든 파일 경로를 스캔하여 CodeFile 엔티티로 변환 후 DB에 저장합니다.
     * 이 메서드는 ReviewSubmissionService에서 호출되어야 합니다.
     * @param submission CodeFile 엔티티를 생성할 대상 ReviewSubmission 엔티티
     */
    void createCodeFilesForSubmission(ReviewSubmission submission);


}
