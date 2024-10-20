package tw.school.rental_recommendationcalculation_model.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "property")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "property_type", nullable = false)
    private String propertyType;

    @Column(name = "area", nullable = false)
    private BigDecimal area;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_time", nullable = false)
    private LocalDateTime modifiedTime;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.modifiedTime == null) {
            this.modifiedTime = LocalDateTime.now();
        }
    }
}
