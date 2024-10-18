package tw.school.rental_backend.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.school.rental_backend.data.dto.DataResponseDTO;
import tw.school.rental_backend.data.dto.PropertyDTO;
import tw.school.rental_backend.data.dto.PropertyDetailDTO;
import tw.school.rental_backend.data.dto.PropertyResponseDTO;
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


    public PropertyController(UserService userService, PropertyService propertyService, RecommendationService recommendationService) {
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
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDirection,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 12) Pageable pageable) {

        try {
            Sort sort;
            if (sortBy != null && !sortBy.isEmpty()) {
                Sort.Direction direction = Sort.Direction.fromString(sortDirection);
                sort = Sort.by(direction, sortBy);
            } else {
                sort = Sort.by(Sort.Direction.DESC, "createdAt");
            }

            Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

            Page<PropertyDTO> propertyPage = propertyService.filterProperties(
                    city, district, road, minPrice, maxPrice, feature, facility, pageableWithSort);

            // 拿資料
            List<PropertyDTO> propertyDTOs = propertyPage.getContent();

            // 判斷是否有下一頁
            Integer nextPage = propertyPage.hasNext() ? propertyPage.getNumber() + 1 : null;

            // 設置總頁數和總共有多少筆資料
            long totalElements = propertyPage.getTotalElements();
            int totalPages = propertyPage.getTotalPages();

            PropertyResponseDTO<List<PropertyDTO>> response = new PropertyResponseDTO<>(propertyDTOs);
            response.setTotalElements(totalElements);
            response.setTotalPages(totalPages);

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
    public ResponseEntity<?> getRecommendation(Authentication authentication, @PageableDefault(sort = "score", size = 12) Pageable pageable) {
        try {

            String username = authentication.getName();
            User user = userService.findByUsername(username);

            PropertyResponseDTO<List<PropertyDTO>> recommendProperty = recommendationService.recommendPropertyForUser(user.getId(), pageable);

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

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<DataResponseDTO<String>> createProperty(@ModelAttribute PropertyForm propertyForm, Authentication authentication) {

        log.info("開始創建房源");
        log.info("主圖片: {}", propertyForm.getMainImage());
        log.info("其他圖片: {}", propertyForm.getImages());

        String username = authentication.getName();

        User user = userService.findByUsername(username);

        propertyForm.setUserId(user.getId());

        propertyService.createProperty(propertyForm);

        DataResponseDTO<String> response = new DataResponseDTO<>("房源新增成功！");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
