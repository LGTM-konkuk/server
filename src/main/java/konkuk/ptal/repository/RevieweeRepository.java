package konkuk.ptal.repository;

import konkuk.ptal.entity.Reviewee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevieweeRepository extends JpaRepository<Reviewee, Long> {
}
