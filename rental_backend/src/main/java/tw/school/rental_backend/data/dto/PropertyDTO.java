package tw.school.rental_backend.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDTO {
    private Long id;
    private String title;
    private String cityName;
    private String districtName;
    private String roadName;
    private String address;
    private int price;
    private String propertyType;
    private String buildingType;
    private BigDecimal area;
    private int floor;
    private String status;
    private String mainImage;
    private LocalDateTime createdAt;
    private PropertyLayoutDTO propertyLayout;
}
