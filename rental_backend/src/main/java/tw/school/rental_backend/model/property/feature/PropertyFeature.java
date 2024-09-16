package tw.school.rental_backend.model.property.feature;

import jakarta.persistence.*;

import lombok.Data;
import tw.school.rental_backend.model.property.Property;

@Data
@Entity
@Table(name = "property_feature")
@IdClass(PropertyFeatureId.class)
public class PropertyFeature {

    @Id
    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Id
    @ManyToOne
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;
}
