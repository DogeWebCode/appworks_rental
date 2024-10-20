package tw.school.rental_backend.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.school.rental_backend.data.dto.UserActionRequest;
import tw.school.rental_backend.model.property.Property;
import tw.school.rental_backend.model.user.User;
import tw.school.rental_backend.service.PropertyService;
import tw.school.rental_backend.service.UserActionService;
import tw.school.rental_backend.util.UserUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user-action")
@Log4j2
public class UserActionController {

    private final UserActionService userActionService;
    private final PropertyService propertyService;
    private final UserUtil userUtil;

    public UserActionController(UserActionService userActionService, PropertyService propertyService, UserUtil userUtil) {
        this.userActionService = userActionService;
        this.propertyService = propertyService;
        this.userUtil = userUtil;
    }

    @PostMapping("/{propertyId}")
    public ResponseEntity<?> recordUserAction(@PathVariable("propertyId") Long propertyId, @RequestBody UserActionRequest request) {
        User user = userUtil.getCurrentUser();
        Property property = propertyService.getPropertyById(propertyId);
        userActionService.recordUserAction(user, property, request.getActionType());

        Map<String, String> response = new HashMap<>();
        response.put("message", "使用者動作紀錄成功");
        return ResponseEntity.ok(response);
    }
}
