package tw.school.rental_backend.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(name = "mobile_phone", nullable = false)
    private String mobilePhone;

    @Column(name = "home_phone")
    private String homePhone;

    private String avatar;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "modified_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedTime;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        modifiedTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedTime = new Date();
    }
}
