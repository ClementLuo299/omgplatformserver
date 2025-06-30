package omgplatform.server.repositories;

import omgplatform.server.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Contains database communication logic for user accounts.
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 * @edited June 29, 2025
 * @since 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    /**
     * Check if an account with some username exists
     *
     * @param username the username that is to be checked
     * @return whether an account with the username exists
     */
    boolean existsByUsername(String username);

    /**
     * Retrieve account by username
     *
     * @param username the username that is to be checked
     * @return the user entity (if found)
     */
    Optional<User> findByUsername(String username);
}
