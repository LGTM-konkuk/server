package konkuk.ptal.service;

import konkuk.ptal.domain.enums.FileNodeType;
import konkuk.ptal.dto.response.*;
import konkuk.ptal.entity.CodeFile;
import konkuk.ptal.entity.ReviewSubmission;
import konkuk.ptal.exception.EntityNotFoundException;
import konkuk.ptal.repository.CodeFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static konkuk.ptal.dto.api.ErrorCode.ENTITY_NOT_FOUND;


@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements IFileService {
    private final CodeFileRepository codeFileRepository;

    @Value("${file.storage.base-path}")
    private String LOCAL_REPO_BASE_DIR_PREFIX;
    private final Map<String, Path> localRepoCache = new ConcurrentHashMap<>();

    @Override
    public ListBranchesResponse getBranches(String gitUrl) {
        File localRepoDir;
        try {
            String defaultBranchForLocalClone = "main";
            localRepoDir = getOrCreateLocalRepo(gitUrl, defaultBranchForLocalClone);
        } catch (IOException | GitAPIException e) {
            log.error("Failed to prepare local repository for getting branches for {}: {}", gitUrl, e.getMessage());
            throw new RuntimeException("Failed to prepare local repository for getting branches: " + e.getMessage(), e);
        }

        try (Git git = Git.open(localRepoDir)) {
            Repository repository = git.getRepository();

            LsRemoteCommand lsRemote = git.lsRemote();
            lsRemote.setHeads(true).setTags(false).setRemote(gitUrl);

            Map<String, Ref> refs = (Map<String, Ref>) lsRemote.call();
            List<GitBranch> branches = new ArrayList<>();
            String defaultBranchName = Constants.MASTER;

            try (RevWalk revWalk = new RevWalk(repository)) {
                for (Map.Entry<String, Ref> entry : refs.entrySet()) {
                    String branchName = Repository.shortenRefName(entry.getKey());
                    ObjectId commitId = entry.getValue().getObjectId();

                    LocalDateTime lastCommitDate = null;
                    String lastCommitMessage = null;

                    try {
                        RevCommit commit = revWalk.parseCommit(commitId);
                        lastCommitDate = LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(commit.getCommitTime()), ZoneId.systemDefault());
                        lastCommitMessage = commit.getFullMessage();
                    } catch (MissingObjectException e) {
                        log.warn("Missing object for commit {} in local repository while getting branches: {}", commitId.name(), e.getMessage());
                    }

                    boolean isDefault = branchName.equals(defaultBranchName);

                    branches.add(GitBranch.from(branchName, isDefault, commitId.name(), lastCommitDate, lastCommitMessage));
                }
            }

            branches.sort(Comparator.comparing(GitBranch::getName));

            return ListBranchesResponse.from(gitUrl, branches, defaultBranchName);

        } catch (GitAPIException | IOException e) {
            log.error("Failed to get branches for {}: {}", gitUrl, e.getMessage());
            throw new RuntimeException("Failed to get branches: " + e.getMessage(), e);
        }
    }

    @Override
    public ProjectFileSystem getProjectFileSystem(String gitUrl, String branch, Long submissionId) {
        File localRepoDir;
        try {
            localRepoDir = getOrCreateLocalRepo(gitUrl, branch);
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException("Failed to prepare local repository: " + e.getMessage(), e);
        }

        try (Git git = Git.open(localRepoDir)) {
            Repository repository = git.getRepository();
            ObjectId head = repository.resolve(Constants.HEAD);

            if (head == null) {
                throw new RuntimeException("HEAD not found for branch: " + branch);
            }

            try (RevWalk revWalk = new RevWalk(repository)) {
                RevCommit commit = revWalk.parseCommit(head);
                RevTree tree = commit.getTree();

                FileNode rootNode = walkTree(git, repository, tree, "");

                Long totalFiles = 0L;
                Long totalSize = 0L;

                try (TreeWalk treeWalk = new TreeWalk(repository)) {
                    treeWalk.addTree(tree);
                    treeWalk.setRecursive(true);
                    while (treeWalk.next()) {
                        if (!treeWalk.isSubtree()) {
                            totalFiles++;
                            try {
                                ObjectId blobId = treeWalk.getObjectId(0);
                                ObjectLoader loader = repository.open(blobId);
                                totalSize += loader.getSize();
                            } catch (MissingObjectException e) {
                                log.warn("Missing object for file {}: {}", treeWalk.getPathString(), e.getMessage());
                            }
                        }
                    }
                }

                return ProjectFileSystem.from(submissionId, branch, rootNode, totalFiles, totalSize);

            }
        } catch (IOException e) {
            log.error("Failed to get project file system from {}: {}", gitUrl, e.getMessage());
            throw new RuntimeException("Failed to get project file system: " + e.getMessage(), e);
        }
    }


    @Override
    public FileContent getFileContent(String gitUrl, String branch, String filePath) {
        File localRepoDir;
        try {
            localRepoDir = getOrCreateLocalRepo(gitUrl, branch);
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException("Failed to prepare local repository: " + e.getMessage(), e);
        }

        try (Git git = Git.open(localRepoDir)) {
            Repository repository = git.getRepository();
            ObjectId head = repository.resolve(Constants.HEAD);

            if (head == null) {
                throw new RuntimeException("HEAD not found for branch: " + branch);
            }

            try (RevWalk revWalk = new RevWalk(repository)) {
                RevCommit commit = revWalk.parseCommit(head);
                RevTree tree = commit.getTree();

                try (TreeWalk treeWalk = TreeWalk.forPath(repository, filePath, tree)) {
                    if (treeWalk == null) {
                        throw new EntityNotFoundException(ENTITY_NOT_FOUND);
                    }

                    ObjectId blobId = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(blobId);

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    loader.copyTo(out);
                    String content = out.toString(StandardCharsets.UTF_8);

                    LocalDateTime lastModified = LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(commit.getCommitTime()), ZoneId.systemDefault());

                    int lineCount = (StandardCharsets.UTF_8.equals(StandardCharsets.UTF_8)) ?
                            content.split("\r\n|\r|\n").length : 0;

                    return FileContent.from(filePath, content, StandardCharsets.UTF_8.name(), loader.getSize(), lastModified, lineCount);
                }
            }
        } catch (IOException e) {
            log.error("Failed to get file content for {}: {}", filePath, e.getMessage());
            throw new RuntimeException("Failed to get file content: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void createCodeFilesForSubmission(ReviewSubmission submission) {
        File localRepoDir;
        try {
            localRepoDir = getOrCreateLocalRepo(submission.getGitUrl(), submission.getBranch());
        } catch (IOException | GitAPIException e) {
            log.error("Failed to prepare local repo for CodeFile creation: {}", e.getMessage());
            throw new RuntimeException("Failed to prepare local repo for CodeFile creation: " + e.getMessage(), e);
        }

        try (Git git = Git.open(localRepoDir)) {
            Repository repository = git.getRepository();
            ObjectId head = repository.resolve(Constants.HEAD);
            if (head == null) {
                throw new RuntimeException("HEAD not found for branch: " + submission.getBranch());
            }

            try (RevWalk revWalk = new RevWalk(repository)) {
                RevCommit commit = revWalk.parseCommit(head);
                RevTree tree = commit.getTree();

                List<CodeFile> codeFilesToSave = new ArrayList<>();
                try (TreeWalk treeWalk = new TreeWalk(repository)) {
                    treeWalk.addTree(tree);
                    treeWalk.setRecursive(true);
                    while (treeWalk.next()) {
                        if (!treeWalk.isSubtree()) {
                            String relativePath = treeWalk.getPathString();
                            codeFilesToSave.add(CodeFile.createCodeFile(submission, relativePath));
                        }
                    }
                }
                codeFileRepository.saveAll(codeFilesToSave);
                log.info("Successfully created {} CodeFile entities for submission {}", codeFilesToSave.size(), submission.getId());
            }
        } catch (IOException e) {
            log.error("Failed to create CodeFile entities for submission {}: {}", submission.getId(), e.getMessage());
            throw new RuntimeException("Failed to create CodeFile entities: " + e.getMessage(), e);
        }
    }

    private File getOrCreateLocalRepo(String gitUrl, String branch) throws IOException, GitAPIException {
        Path localRepoPath;
        String repoDirName = generateSafeRepoDirName(gitUrl);
        Path baseDir = getOrCreateBaseDir();

        localRepoPath = baseDir.resolve(repoDirName);

        File localRepoDir = localRepoPath.toFile();

        if (!localRepoDir.exists()) {
            log.info("Cloning new repository: {} to {}", gitUrl, localRepoPath);
            try {
                Git.cloneRepository()
                        .setURI(gitUrl)
                        .setDirectory(localRepoDir)
                        .setBare(false)
                        .call();
            } catch (GitAPIException e) {
                log.error("Failed to clone repository {}: {}", gitUrl, e.getMessage());
                throw new RuntimeException("Failed to clone repository: " + gitUrl + " - " + e.getMessage(), e);
            }
        } else {
            log.info("Opening existing repository: {} at {}", gitUrl, localRepoPath);
            try (Git git = Git.open(localRepoDir)) {
                log.info("Fetching and checking out branch {} for {}", branch, gitUrl);
                git.fetch()
                        // .setCredentialsProvider(credentialsProvider) // Private Repo 시 필요
                        .call();
                if (git.getRepository().findRef(Constants.R_HEADS + branch) == null) {
                    git.checkout()
                            .setCreateBranch(true)
                            .setName(branch)
                            .setStartPoint("origin/" + branch)
                            .call();
                } else {
                    git.checkout().setName(branch).call();
                }
            } catch (GitAPIException e) {
                log.error("Failed to fetch/checkout branch {} for {}: {}", branch, gitUrl, e.getMessage());
                throw new RuntimeException("Failed to update repository: " + gitUrl + " - " + e.getMessage(), e);
            }
        }
        localRepoCache.put(gitUrl, localRepoPath);
        return localRepoDir;
    }

    private String generateSafeRepoDirName(String gitUrl) {
        String name = gitUrl.replaceAll("[^a-zA-Z0-9.-]", "_");
        if (name.endsWith(".git")) {
            name = name.substring(0, name.length() - 4);
        }
        return name;
    }

    private Path getOrCreateBaseDir() throws IOException {
        // TODO: 운영 환경에서는 특정 고정된 경로를 사용하도록 설정해야 합니다.
        // TODO: 디스크 공간 관리, 오래된 클론 삭제 등 전략 필요
        Path baseDir = Paths.get(System.getProperty("java.io.tmpdir")).resolve(LOCAL_REPO_BASE_DIR_PREFIX);
        if (!Files.exists(baseDir)) {
            Files.createDirectories(baseDir);
        }
        return baseDir;
    }

    private FileNode walkTree(Git git, Repository repository, RevTree tree, String currentPath) throws IOException {
        List<FileNode> children = new ArrayList<>();

        try (TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(false);

            while (treeWalk.next()) {
                String entryName = treeWalk.getNameString();
                String entryPath = currentPath.isEmpty() ? entryName : currentPath + "/" + entryName;
                ObjectId objectId = treeWalk.getObjectId(0);

                if (treeWalk.isSubtree()) {
                    try (RevWalk subRevWalk = new RevWalk(repository)) {
                        RevTree subTree = subRevWalk.parseTree(objectId);
                        FileNode dirNode = walkTree(git, repository, subTree, entryPath);
                        dirNode = FileNode.from(entryName, entryPath, FileNodeType.DIRECTORY, null, null, dirNode.getChildren());
                        children.add(dirNode);
                    } catch (MissingObjectException e) {
                        log.warn("Missing tree object for path {}: {}", entryPath, e.getMessage());
                    }
                } else {
                    ObjectLoader loader = repository.open(objectId);
                    Long fileSize = loader.getSize();

                    LocalDateTime lastModified = null;
                    try {
                        Iterable<RevCommit> commits = git.log()
                                .addPath(entryPath)
                                .setMaxCount(1)
                                .call();
                        if (commits.iterator().hasNext()) {
                            RevCommit lastFileCommit = commits.iterator().next();
                            lastModified = LocalDateTime.ofInstant(
                                    Instant.ofEpochSecond(lastFileCommit.getCommitTime()), ZoneId.systemDefault());
                        }
                    } catch (GitAPIException e) {
                        log.warn("Failed to get last commit for file {}: {}", entryPath, e.getMessage());
                    }

                    FileNode fileNode = FileNode.from(entryName, entryPath, FileNodeType.FILE, fileSize, lastModified, null);
                    children.add(fileNode);
                }
            }
        }
        children.sort(Comparator
                .comparing((FileNode node) -> node.getType() == FileNodeType.DIRECTORY ? 0 : 1)
                .thenComparing(FileNode::getName));

        return FileNode.from(currentPath.isEmpty() ? "root" : new File(currentPath).getName(), currentPath, FileNodeType.DIRECTORY, null, null, children);
    }
}
