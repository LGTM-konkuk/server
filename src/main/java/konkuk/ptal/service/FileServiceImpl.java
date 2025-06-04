package konkuk.ptal.service;

import konkuk.ptal.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static konkuk.ptal.dto.api.ErrorCode.*;

@Service
@Slf4j
public class FileServiceImpl implements IFileService{

    @Value("${file.storage.base-path}")
    private String baseStoragePath;

    @Override
    public String saveCode(String githubAddress, String branchName) {

        String repoName = extractRepoName(githubAddress);
        if (repoName == null) {
            throw new BadRequestException(REPO_NAME_EXTRACTION_FAILED);
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Path targetPath = Paths.get(baseStoragePath, repoName + "_" + timestamp);
        File targetDir = targetPath.toFile();
        try {
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            CloneCommand cloneCommand = Git.cloneRepository()
                    .setURI(githubAddress)
                    .setDirectory(targetDir);

            if (branchName != null && !branchName.trim().isEmpty()) {
                cloneCommand.setBranch(branchName)
                        .setBranchesToClone(List.of(Constants.R_HEADS + branchName));
            }

            cloneCommand.call();
            System.out.println("Repository cloned successfully to: " + targetPath.toAbsolutePath().toString());
            return targetPath.toAbsolutePath().toString();

        } catch (GitAPIException e) {
            System.err.println("Error during JGit clone operation: " + e.getMessage());
            cleanupDirectory(targetPath, targetDir);
            throw new BadRequestException(REPO_CLONE_FAILED);
        }
    }

    private String extractRepoName(String githubAddress) {
        if (githubAddress == null || githubAddress.trim().isEmpty()) {
            return null;
        }
        int lastSlash = githubAddress.lastIndexOf('/');
        int lastDotGit = githubAddress.lastIndexOf(".git");

        String name = githubAddress;
        if (lastSlash != -1) {
            name = name.substring(lastSlash + 1);
        }
        if (lastDotGit != -1) {
            name = name.substring(0, lastDotGit);
        }
        return name.isEmpty() ? null : name;
    }

    private void cleanupDirectory(Path targetPath, File targetDir) {
        try {
            if (targetDir.exists()) {
                Files.walk(targetPath)
                        .sorted(java.util.Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException cleanupException) {
            log.error("Error cleaning up directory {} after failed operation: {}", targetPath.toAbsolutePath(), cleanupException.getMessage(), cleanupException);
        }
    }

    @Override
    public String getCodeFile(String absolutePath, String relativeFilePath) {
        Path filePath = Paths.get(absolutePath, relativeFilePath);
        try {
            if (Files.exists(filePath) && Files.isReadable(filePath)) {
                return Files.readString(filePath);
            } else {
                System.err.println("File not found or not readable: " + filePath.toAbsolutePath());
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error reading code file: " + filePath.toAbsolutePath() + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<String> getCodeFileList(String absolutePath) {
        Path rootPath = Paths.get(absolutePath);
        if (!Files.exists(rootPath) || !Files.isDirectory(rootPath)) {
            throw new BadRequestException(INVALID_REPO_PATH);
        }
        try (Stream<Path> walk = Files.walk(rootPath)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(p -> !p.toString().contains(File.separator + ".git" + File.separator))
                    .filter(p -> !p.toString().contains(File.separator + ".idea" + File.separator))
                    .map(rootPath::relativize)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new BadRequestException(FILE_LIST_ERROR);
        }
    }
}
