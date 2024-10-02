package tw.school.rental_backend.model.user;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import tw.school.rental_backend.model.property.Property;

@Data
@Entity
@Table(name = "user_action")
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Column(name = "action_time", nullable = false)
    private LocalDateTime actionTime;
}

