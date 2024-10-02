package tw.school.rental_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.school.rental_backend.data.dto.DataResponseDTO;
import tw.school.rental_backend.data.dto.FeatureDTO;
import tw.school.rental_backend.model.property.feature.Feature;
import tw.school.rental_backend.service.FeatureService;

import java.util.List;

@RestController
@RequestMapping("/api/feature")
public class FeatureController {

    private final FeatureService featureService;

    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }

    @GetMapping
    public ResponseEntity<?> getFeature() {
        List<FeatureDTO> feature = featureService.findAllFeatures();
        DataResponseDTO<List<FeatureDTO>> response = new DataResponseDTO<>(feature);
        return ResponseEntity.ok(response);
    }
}
