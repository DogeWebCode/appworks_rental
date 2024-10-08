package tw.school.rental_backend.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "使用者名稱不可為空")
    @Column(nullable = false)
    private String username;

    @Email(message = "請輸入有效的電子信箱")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "密碼不可為空")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Pattern(regexp = "^[0-9]{10}$", message = "請輸入有效的手機號碼")
    @Column(name = "mobile_phone", nullable = false)
    private String mobilePhone;

    @Pattern(regexp = "^[0-9]{10}$", message = "請輸入有效的住家電話")
    @Column(name = "home_phone")
    private String homePhone;

    private String picture;

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
