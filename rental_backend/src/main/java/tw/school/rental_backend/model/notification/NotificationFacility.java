package tw.school.rental_backend.model.notification;

import jakarta.persistence.*;
import lombok.Data;
import tw.school.rental_backend.model.property.facility.Facility;

@Data
@Entity
@Table(name = "notification_facility")
public class NotificationFacility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;
}
