package tw.school.rental_backend.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserActionDTO {
    private Long userId;
    private Long propertyId;
    private String actionType;
    private LocalDateTime actionTime;
}
