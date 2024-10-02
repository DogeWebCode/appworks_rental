package tw.school.rental_backend.data.dto.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import tw.school.rental_backend.data.dto.PropertyLayoutDTO;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyForm {
    private Long userId;
    private String title;
    private String cityName;
    private String districtName;
    private String roadName;
    private String address;
    private Integer price;
    private String propertyType;
    private String buildingType;
    private String lessor;
    private BigDecimal area;
    private Integer floor;
    private Integer totalFloor;
    private String status;
    private MultipartFile mainImage;
    private String description;
    private Integer deposit;
    private Integer managementFee;
    private String rentPeriod;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private List<String> features;
    private List<String> facilities;
    private List<MultipartFile> images;
    private int roomCount;
    private int livingRoomCount;
    private int bathroomCount;
    private int balconyCount;
    private int kitchenCount;

}
