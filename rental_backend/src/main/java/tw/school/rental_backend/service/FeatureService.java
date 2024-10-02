package tw.school.rental_backend.service;

import tw.school.rental_backend.data.dto.FeatureDTO;

import java.util.List;

public interface FeatureService {
    List<FeatureDTO> findAllFeatures();
}
