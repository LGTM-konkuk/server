package konkuk.ptal.service;

import java.util.List;

public interface IFileService {

    /**
     * 코드 파일을 깃허브로부터 다운로드 받아서 저장합니다.
     * @param githubAddress 깃허브 주소
     */
    String saveCode(String githubAddress, String branchName);

    /**
     * 특정 코드파일을 조회하기 위해 사용합니다.
     * @param absolutePath 코드가 저장되어있는 절대경로(프로젝트 루트까지의 경로)
     * @param relativeFilePath 절대경로 뒤로 붙는 상대경로
     */
    String getCodeFile(String absolutePath, String relativeFilePath);

    /**
     * 모든 코드 파일을 조회하기 위해 사용합니다.
     * @param absolutePath 코드가 저장되어있는 절대경로(프로젝트 루트까지의 경로)
     * @return 상대경로 목록을 반환합니다. (java.util.List)
     */
    List<String> getCodeFileList(String absolutePath);

}
