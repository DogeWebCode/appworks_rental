package tw.school.rental_backend.model.geo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "city", uniqueConstraints = {@UniqueConstraint(columnNames = {"city_name"})})
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city_name", nullable = false)
    private String cityName;
}
