package tw.school.rental_backend.model.property.facility;

import lombok.Data;

import java.io.Serializable;

@Data
public class PropertyFacilityId implements Serializable {
    private Long property;
    private Long facility;
}
