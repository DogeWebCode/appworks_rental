package tw.school.rental_backend.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDTO {
    private Long id;
    private Long propertyId;
    private String title;
    private String cityName;
    private String districtName;
    private String roadName;
    private int price;
    private String propertyType;
    private String mainImage;
    private LocalDateTime createdAt;
}
