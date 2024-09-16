package tw.school.rental_backend.model.notification;

import jakarta.persistence.*;
import lombok.Data;
import tw.school.rental_backend.model.location.City;
import tw.school.rental_backend.model.location.District;
import tw.school.rental_backend.model.user.User;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rent_min", nullable = false)
    private int rentMin;

    @Column(name = "rent_max", nullable = false)
    private int rentMax;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(name = "property_type", nullable = false)
    private String propertyType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_time", nullable = false)
    private LocalDateTime modifiedTime;
}
