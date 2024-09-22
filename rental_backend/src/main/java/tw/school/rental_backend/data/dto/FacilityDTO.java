package tw.school.rental_backend.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacilityDTO {
    private Long id;
    private String facilityName;
    private String iconUrl;
}
