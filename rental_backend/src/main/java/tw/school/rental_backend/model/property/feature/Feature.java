package tw.school.rental_backend.model.property.feature;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "feature")
public class Feature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "feature_name", nullable = false)
    private String featureName;

    @Column(name = "icon_url")
    private String iconUrl;
}
