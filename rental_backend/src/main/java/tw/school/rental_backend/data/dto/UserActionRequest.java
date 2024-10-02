package tw.school.rental_backend.data.dto;

import lombok.Data;

@Data
public class UserActionRequest {
    private Long userId;
    private Long propertyId;
    private String actionType;
}
