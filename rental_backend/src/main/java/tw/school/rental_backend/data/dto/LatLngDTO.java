package tw.school.rental_backend.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LatLngDTO {
    private Double lat;
    private Double lng;
}
