package tw.school.rental_backend.model.property;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "property_layout")
public class PropertyLayout {
    @Id
    @Column(name = "property_id")
    private Long propertyId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "property_id")
    private Property property;

    @Column(name = "room_count", nullable = false)
    private int roomCount;

    @Column(name = "living_room_count", nullable = false)
    private int livingRoomCount;

    @Column(name = "bathroom_count", nullable = false)
    private int bathroomCount;

    @Column(name = "balcony_count", nullable = false)
    private int balconyCount;

    @Column(name = "kitchen_count", nullable = false)
    private int kitchenCount;
}
