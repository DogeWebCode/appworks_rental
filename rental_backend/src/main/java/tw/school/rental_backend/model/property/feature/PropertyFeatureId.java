package tw.school.rental_backend.model.property.feature;

import lombok.Data;

import java.io.Serializable;

@Data
public class PropertyFeatureId implements Serializable {
    private Long property;
    private Long feature;
}
