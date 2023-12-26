package booking.repository;

import booking.entity.Role;
import booking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT U FROM User U WHERE NOT :adminRole MEMBER OF U.roles AND U.username LIKE %:username%")
    Page<User> findAllByRoles(@Param("username") String username,
                              @Param("adminRole") Role adminRole,
                              Pageable pageable);

    User findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
