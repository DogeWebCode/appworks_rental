package tw.school.rental_backend.model.location;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "road", uniqueConstraints = {@UniqueConstraint(columnNames = {"road_name", "district_id"})})
public class Road {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "road_name", nullable = false)
    private String roadName;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;
}

