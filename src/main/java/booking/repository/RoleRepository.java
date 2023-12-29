package booking.repository;

import booking.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;


public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByRoleName(String roleName);

    @Query("SELECT Role FROM Role WHERE roleName ='ADMIN'")
    boolean checkRoleAdmin(Set<Role> roles);
}
