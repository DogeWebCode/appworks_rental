package tw.school.rental_backend.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DistrictDTO {
    private Long id;
    private String districtName;
}
