package konkuk.ptal.repository;

import konkuk.ptal.entity.CodeFile;
import konkuk.ptal.entity.ReviewSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodeFileRepository extends JpaRepository<CodeFile, Long> {
    Optional<CodeFile> findBySubmissionIdAndRelativePath(ReviewSubmission submission, String relativePath);
}
