package tw.school.rental_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.school.rental_backend.data.dto.DataResponseDTO;
import tw.school.rental_backend.data.dto.FacilityDTO;
import tw.school.rental_backend.service.FacilityService;

import java.util.List;

@RestController
@RequestMapping("/api/facility")
public class FacilityController {

    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @GetMapping
    public ResponseEntity<?> getFacility() {
        List<FacilityDTO> facility = facilityService.findAllFacilities();
        DataResponseDTO<List<FacilityDTO>> response = new DataResponseDTO<>(facility);
        return ResponseEntity.ok(response);
    }
}
