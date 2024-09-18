package tw.school.rental_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tw.school.rental_backend.data.dto.PropertyDTO;

public interface PropertyService {

    Page<PropertyDTO> filterProperties(String city, String district, String road,
                                       Integer minPrice, Integer maxPrice,
                                       String[] features, String[] equipment,
                                       Pageable pageable);
}
