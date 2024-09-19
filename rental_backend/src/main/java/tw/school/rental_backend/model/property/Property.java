package tw.school.rental_backend.model.property;

import jakarta.persistence.*;
import lombok.Data;
import tw.school.rental_backend.model.geo.City;
import tw.school.rental_backend.model.geo.District;
import tw.school.rental_backend.model.geo.Road;
import tw.school.rental_backend.model.property.facility.PropertyFacility;
import tw.school.rental_backend.model.property.feature.PropertyFeature;
import tw.school.rental_backend.model.property.image.PropertyImage;
import tw.school.rental_backend.model.user.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "property")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @ManyToOne
    @JoinColumn(name = "road_id", nullable = false)
    private Road road;

    @Column(name = "address")
    private String address;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "deposit", nullable = false)
    private Integer deposit;

    @Column(name = "management_fee")
    private Integer managementFee;

    @Column(name = "rent_period", nullable = false)
    private String rentPeriod;

    @Column(name = "property_type", nullable = false)
    private String propertyType;

    @Column(name = "building_type", nullable = false)
    private String buildingType;

    @Column(name = "area", nullable = false)
    private BigDecimal area;

    @Column(name = "floor", nullable = false)
    private Integer floor;

    @Column(name = "total_floor", nullable = false)
    private Integer totalFloor;

    @Column(name = "lessor", nullable = true)
    private String lessor;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "main_image", nullable = false)
    private String mainImage;

    @Column(name = "latitude", nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false)
    private BigDecimal longitude;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyFacility> facility;  // 設備關聯

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyImage> image;  // 圖片關聯

    @OneToMany(mappedBy = "property", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyFeature> feature;

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
