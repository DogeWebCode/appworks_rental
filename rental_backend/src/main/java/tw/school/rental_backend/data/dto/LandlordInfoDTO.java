package tw.school.rental_backend.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LandlordInfoDTO {
    @JsonProperty("landlord_id")
    private Long id;
    @JsonProperty("landlord_username")
    private String username;
    @JsonProperty("landlord_role")
    private String role;
    @JsonProperty("landlord_mobile_phone")
    private String mobilePhone;
    @JsonProperty("landlord_picture")
    private String picture;
}
