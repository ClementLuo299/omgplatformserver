package omgplatform.server.repositories;

import omgplatform.server.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Contains database communication logic for user accounts.
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByUsername(String username);
}
