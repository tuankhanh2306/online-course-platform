package edu.uth.online_course_platform.repositories;

import edu.uth.online_course_platform.models.User; // <- QUAN TRỌNG: Đảm bảo import đúng lớp User này
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// Vấn đề nằm ở đây. Nó PHẢI là JpaRepository<User, Long>
// trong đó 'User' là lớp model của bạn, không phải của Spring Security.
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}