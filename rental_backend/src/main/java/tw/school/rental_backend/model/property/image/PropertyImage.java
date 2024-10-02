package tw.school.rental_backend.model.property.image;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.school.rental_backend.model.property.Property;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "property_image")
public class PropertyImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_time", nullable = false)
    private LocalDateTime modifiedTime;

    public PropertyImage(Property property, String imageUrl) {
        this.property = property;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
        this.modifiedTime = LocalDateTime.now();
    }
}
