package tw.school.rental_backend.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.school.rental_backend.data.dto.PropertyDTO;
import tw.school.rental_backend.data.dto.ResponseDTO;
import tw.school.rental_backend.error.ErrorResponse;
import tw.school.rental_backend.model.user.User;
import tw.school.rental_backend.service.RecommendationService;
import tw.school.rental_backend.service.UserService;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/property")
public class PropertyController {

    private final RecommendationService recommendationService;
    private final UserService userService;


    public PropertyController(RecommendationService recommendationService, UserService userService) {
        this.recommendationService = recommendationService;
        this.userService = userService;
    }

    @GetMapping("/recommendation")
    public ResponseEntity<?> getRecommendation(Authentication authentication, @PageableDefault() Pageable pageable) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            ResponseDTO<List<PropertyDTO>> recommendProperty = recommendationService.recommendPropertyForUser(user.getId(), pageable);
            return ResponseEntity.ok(recommendProperty);
        } catch (RuntimeException e) {
            log.error("推薦系統發生錯誤：{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("推薦失敗"));
        }
    }
}
