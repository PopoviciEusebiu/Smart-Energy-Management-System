package sd.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sd.user.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {
    @Query("SELECT role FROM Role role WHERE role.role = :role")
    Role findByRole(@Param("role") String role);
}
