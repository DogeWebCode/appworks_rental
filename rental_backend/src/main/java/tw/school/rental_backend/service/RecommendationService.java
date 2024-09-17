package tw.school.rental_backend.service;

import org.springframework.data.domain.Pageable;
import tw.school.rental_backend.data.dto.PropertyDTO;
import tw.school.rental_backend.data.dto.ResponseDTO;

import java.util.List;

public interface RecommendationService {

    ResponseDTO<List<PropertyDTO>> recommendPropertyForUser(Long userId, Pageable pageable);
}
