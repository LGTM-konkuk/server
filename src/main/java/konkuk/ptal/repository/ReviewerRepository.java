package konkuk.ptal.repository;

import konkuk.ptal.entity.Reviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewerRepository extends JpaRepository<Reviewer, Long> {
    Optional<Reviewer> findByUser_Id(Long userId);

} 