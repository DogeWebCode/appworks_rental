package tw.school.rental_backend.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.school.rental_backend.data.dto.UserActionRequest;
import tw.school.rental_backend.model.property.Property;
import tw.school.rental_backend.model.user.User;
import tw.school.rental_backend.service.PropertyService;
import tw.school.rental_backend.service.UserActionService;
import tw.school.rental_backend.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user-action")
@Log4j2
public class UserActionController {

    private final UserActionService userActionService;
    private final UserService userService;
    private final PropertyService propertyService;

    public UserActionController(UserActionService userActionService, UserService userService, PropertyService propertyService) {
        this.userActionService = userActionService;
        this.userService = userService;
        this.propertyService = propertyService;
    }

    @PostMapping("/{propertyId}")
    public ResponseEntity<?> recordUserAction(@PathVariable("propertyId") Long propertyId, @RequestBody UserActionRequest request, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        Property property = propertyService.getPropertyById(propertyId);

        try {
            userActionService.recordUserAction(user, property, request.getActionType());
            Map<String, String> response = new HashMap<>();
            response.put("message", "使用者動作紀錄成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("記錄使用者動作時發生錯誤", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "記錄使用者動作時發生錯誤: " + e.getMessage());
            errorResponse.put("exceptionType", e.getClass().getName());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
