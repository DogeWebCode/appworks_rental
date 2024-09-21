package tw.school.rental_backend.service;

import org.springframework.data.domain.Pageable;
import tw.school.rental_backend.data.dto.PropertyDTO;
import tw.school.rental_backend.data.dto.PropertyResponseDTO;

import java.util.List;

public interface RecommendationService {

    PropertyResponseDTO<List<PropertyDTO>> recommendPropertyForUser(Long userId, Pageable pageable);

}
