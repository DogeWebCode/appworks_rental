package tw.school.rental_backend.model.property.facility;

import jakarta.persistence.*;

import lombok.Data;
import tw.school.rental_backend.model.property.Property;

@Data
@Entity
@Table(name = "property_facility")
@IdClass(PropertyFacilityId.class)
public class PropertyFacility {

    @Id
    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Id
    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;
}
