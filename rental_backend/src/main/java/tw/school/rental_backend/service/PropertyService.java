package tw.school.rental_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tw.school.rental_backend.data.dto.PropertyDTO;
import tw.school.rental_backend.data.dto.PropertyDetailDTO;
import tw.school.rental_backend.data.dto.form.PropertyForm;
import tw.school.rental_backend.model.property.Property;

public interface PropertyService {

    Page<PropertyDTO> filterProperties(String city, String district, String road,
                                       Integer minPrice, Integer maxPrice,
                                       String[] features, String[] equipment,
                                       Pageable pageable);

    PropertyDetailDTO getPropertyDetail(Long propertyId);

    void createProperty(PropertyForm propertyForm);

    Property getPropertyById(Long propertyId);
}
