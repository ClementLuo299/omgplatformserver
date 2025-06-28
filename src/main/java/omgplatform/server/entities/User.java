package omgplatform.server.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * Represents a user account entity.
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 * @edited June 27, 2025
 * @since 1.0
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    //COLUMNS

    //User id
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Username
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    //Password
    @Column(name = "password", nullable = false)
    private String password;

    //Full name
    @Column(name = "full_name")
    private String fullName;

    //Date of birth
    @Column(name = "date_of_birth")
    private java.time.LocalDate dateOfBirth;

    //Last login
    @Column(name = "last_login")
    private OffsetDateTime lastLogin;

    //Account created at
    @CreationTimestamp
    @Column(name = "created_at",updatable = false)
    private OffsetDateTime created_at;

    //Account last updated at
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updated_at;
}
