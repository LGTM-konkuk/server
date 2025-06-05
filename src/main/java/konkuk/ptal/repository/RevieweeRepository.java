package konkuk.ptal.repository;

import konkuk.ptal.entity.Reviewee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RevieweeRepository extends JpaRepository<Reviewee, Long> {
    Optional<Reviewee> findByUser_Id(Long userId);
}
