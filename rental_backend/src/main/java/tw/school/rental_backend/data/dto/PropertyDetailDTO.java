package tw.school.rental_backend.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDetailDTO extends PropertyDTO {
    private String description;
    private Integer deposit;
    private Long userId;

    @JsonProperty("management_fee")
    private Integer managementFee;

    @JsonProperty("rent_period")
    private String rentPeriod;

    @JsonProperty("building_type")
    private String buildingType;

    @JsonProperty("total_floor")
    private Integer totalFloor;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private List<String> facility;
    private List<String> images;

    @JsonProperty("landlord_info")
    private LandlordInfoDTO landlordInfo;

}
