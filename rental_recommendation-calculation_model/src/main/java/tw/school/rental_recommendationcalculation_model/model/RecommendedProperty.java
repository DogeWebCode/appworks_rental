package tw.school.rental_recommendationcalculation_model.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "recommended_property", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "property_id"})})
@AllArgsConstructor
@NoArgsConstructor
public class RecommendedProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    // 添加 score 屬性
    @Column(name = "score", nullable = false)
    private Integer score;

    public RecommendedProperty(Long userId, Long propertyId, Integer score) {
        this.userId = userId;
        this.propertyId = propertyId;
        this.score = score;
    }
}
