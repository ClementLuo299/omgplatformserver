package omgplatform.server.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Represents a user account entity.
 *
 * @authors Clement Luo,
 * @date April 15, 2025
 */
@Entity
@Table(name = "users")
public class User {

    //COLUMNS

    //User id
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Username
    @Column(name = "username")
    private String username;

    //Password
    @Column(name = "password")
    private String password;

    //Account created at
    @Column(name = "created_at")
    private LocalDateTime created_at;

    //Account last updated at
    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    public User() {}

    //SETTERS AND GETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}
