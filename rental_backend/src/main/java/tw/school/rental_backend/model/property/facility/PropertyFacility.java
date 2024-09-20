package tw.school.rental_backend.model.property.facility;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.school.rental_backend.model.property.Property;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "property_facility")
@IdClass(PropertyFacilityId.class)
public class PropertyFacility {

    @Id
    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    @JsonBackReference
    private Property property;

    @Id
    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

}
