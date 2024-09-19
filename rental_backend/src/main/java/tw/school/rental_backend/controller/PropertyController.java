package tw.school.rental_backend.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tw.school.rental_backend.data.dto.PropertyDTO;
import tw.school.rental_backend.data.dto.PropertyDetailDTO;
import tw.school.rental_backend.data.dto.ResponseDTO;
import tw.school.rental_backend.data.dto.form.PropertyForm;
import tw.school.rental_backend.error.ErrorResponse;
import tw.school.rental_backend.model.user.User;
import tw.school.rental_backend.service.PropertyService;
import tw.school.rental_backend.service.RecommendationService;
import tw.school.rental_backend.service.UserService;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/property")
public class PropertyController {

    private final RecommendationService recommendationService;
    private final PropertyService propertyService;
    private final UserService userService;


    public PropertyController(RecommendationService recommendationService, UserService userService, PropertyService propertyService) {
        this.recommendationService = recommendationService;
        this.userService = userService;
        this.propertyService = propertyService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String road,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String[] feature,
            @RequestParam(required = false) String[] facility,
            @PageableDefault() Pageable pageable) {

        try {
            // 這裡 return Page<PropertyDTO>
            Page<PropertyDTO> propertyPage = propertyService.filterProperties(
                    city, district, road, minPrice, maxPrice, feature, facility, pageable);

            // 拿資料
            List<PropertyDTO> propertyDTOs = propertyPage.getContent();

            // 判斷是否有下一頁
            Integer nextPage = propertyPage.hasNext() ? propertyPage.getNumber() + 1 : null;

            ResponseDTO<List<PropertyDTO>> response = new ResponseDTO<>(propertyDTOs);

            if (nextPage != null) {
                response.setNextPage(nextPage.toString());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("篩選功能發生錯誤：{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("篩選失敗"));
        }
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

    @GetMapping("/detail/{propertyId}")
    public ResponseEntity<?> getPropertyDetail(@PathVariable Long propertyId) {
        PropertyDetailDTO propertyDetail = propertyService.getPropertyDetail(propertyId);
        return ResponseEntity.ok(propertyDetail);
    }

    @PostMapping(path = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<String> createProperty(@ModelAttribute PropertyForm propertyForm) {
        // 獲取當前使用者
        String username = getCurrentUserUsername();

        // 通過使用者名稱獲取使用者對象
        User user = userService.findByUsername(username);

        // 設置 userId 到 PropertyForm 中
        propertyForm.setUserId(user.getId());

        // 創建房源
        propertyService.createProperty(propertyForm);

        return new ResponseEntity<>("Property created successfully!", HttpStatus.CREATED);
    }

    // TODO : 挪方法到 Service
    // 獲取當前登入的使用者名稱
    private String getCurrentUserUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
