package tw.school.rental_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<?> recordUserAction(@PathVariable("propertyId") Long propertyId, @RequestBody UserActionRequest request) {
        String username = getCurrentUserUsername();
        User user = userService.findByUsername(username);

        Property property = propertyService.getPropertyById(propertyId);

        userActionService.recordUserAction(user, property, request.getActionType());

        Map<String, String> response = new HashMap<>();
        response.put("message", "使用者動作紀錄成功");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<?> removeFavoriteAction(@PathVariable("propertyId") Long propertyId) {
        String username = getCurrentUserUsername();
        User user = userService.findByUsername(username);

        userActionService.removeFavoriteAction(user.getId(), propertyId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "刪除使用者收藏紀錄成功");

        return ResponseEntity.ok(response);
    }


    // 獲取當前使用者的用戶名
    private String getCurrentUserUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
