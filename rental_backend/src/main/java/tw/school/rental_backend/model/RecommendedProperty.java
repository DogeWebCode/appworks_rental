package tw.school.rental_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recommended_property", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "property_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedProperty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
