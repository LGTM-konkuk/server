package konkuk.ptal.repository;

import konkuk.ptal.entity.ReviewSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewSessionRepository extends JpaRepository<ReviewSubmission, Long> {
}
