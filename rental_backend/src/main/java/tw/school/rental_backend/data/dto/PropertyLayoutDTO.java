package tw.school.rental_backend.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropertyLayoutDTO {
    private int livingRoomCount;
    private int bathroomCount;
    private int balconyCount;
    private int kitchenCount;
}
