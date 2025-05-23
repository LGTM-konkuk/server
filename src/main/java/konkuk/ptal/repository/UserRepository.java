package konkuk.ptal.repository;

import konkuk.ptal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 사용자 존재 여부 확인
    boolean existsByEmail(String email);

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

}
