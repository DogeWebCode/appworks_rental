package tw.school.rental_backend.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.school.rental_backend.data.dto.*;
import tw.school.rental_backend.data.dto.form.PropertyForm;
import tw.school.rental_backend.model.user.User;
import tw.school.rental_backend.service.PropertyService;
import tw.school.rental_backend.service.RecommendationService;
import tw.school.rental_backend.util.UserUtil;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/property")
public class PropertyController {

    private final RecommendationService recommendationService;
    private final PropertyService propertyService;
    private final UserUtil userUtil;

    public PropertyController(PropertyService propertyService, RecommendationService recommendationService, UserUtil userUtil) {
        this.recommendationService = recommendationService;
        this.propertyService = propertyService;
        this.userUtil = userUtil;
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

        Sort sort = (sortBy != null && !sortBy.isEmpty())
                ? Sort.by(Sort.Direction.fromString(sortDirection), sortBy)
                : Sort.by(Sort.Direction.DESC, "createdAt");

        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<PropertyDTO> propertyPage = propertyService.filterProperties(
                city, district, road, minPrice, maxPrice, feature, facility, pageableWithSort);

        List<PropertyDTO> propertyDTOs = propertyPage.getContent();
        Integer nextPage = propertyPage.hasNext() ? propertyPage.getNumber() + 1 : null;
        long totalElements = propertyPage.getTotalElements();
        int totalPages = propertyPage.getTotalPages();

        PropertyResponseDTO<List<PropertyDTO>> response = new PropertyResponseDTO<>(propertyDTOs);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        if (nextPage != null) {
            response.setNextPage(nextPage.toString());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommendation")
    public ResponseEntity<?> getRecommendation(@PageableDefault(sort = "score", size = 12) Pageable pageable) {
        User user = userUtil.getCurrentUser();
        PropertyResponseDTO<List<PropertyDTO>> recommendProperty = recommendationService.recommendPropertyForUser(user.getId(), pageable);
        return ResponseEntity.ok(recommendProperty);
    }

    @GetMapping("/detail/{propertyId}")
    public ResponseEntity<?> getPropertyDetail(@PathVariable Long propertyId) {
        PropertyDetailDTO propertyDetail = propertyService.getPropertyDetail(propertyId);
        return ResponseEntity.ok(propertyDetail);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<DataResponseDTO<String>> createProperty(@ModelAttribute PropertyForm propertyForm) {
        log.info("開始創建房源");
        log.info("主圖片: {}", propertyForm.getMainImage());
        log.info("其他圖片: {}", propertyForm.getImages());

        User user = userUtil.getCurrentUser();
        propertyForm.setUserId(user.getId());
        propertyService.createProperty(propertyForm);

        DataResponseDTO<String> response = new DataResponseDTO<>("房源新增成功！");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
