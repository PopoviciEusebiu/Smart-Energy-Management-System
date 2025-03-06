package sd.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sd.user.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Boolean existsUserByUsername(String username);
    Optional<User> findUserByUsername(String username);
    User findByUsername(String username);


}
