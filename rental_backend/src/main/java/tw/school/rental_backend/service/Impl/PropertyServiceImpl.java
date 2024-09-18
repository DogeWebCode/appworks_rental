package tw.school.rental_backend.service.Impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tw.school.rental_backend.data.dto.PropertyDTO;
import tw.school.rental_backend.mapper.PropertyMapper;
import tw.school.rental_backend.model.property.Property;
import tw.school.rental_backend.repository.jpa.property.PropertyRepository;
import tw.school.rental_backend.service.PropertyService;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;

    public PropertyServiceImpl(PropertyRepository propertyRepository, PropertyMapper propertyMapper) {
        this.propertyRepository = propertyRepository;
        this.propertyMapper = propertyMapper;
    }

    @Override
    public Page<PropertyDTO> filterProperties(
            String city,
            String district,
            String road,
            Integer minPrice,
            Integer maxPrice,
            String[] features,
            String[] facility,
            Pageable pageable) {

        Specification<Property> spec = Specification.where(null);

        // 動態添加條件
        if (city != null) {
            spec = spec.and(PropertySpecification.hasCity(city));
        }

        if (district != null) {
            spec = spec.and(PropertySpecification.hasDistrict(district));
        }

        if (road != null) {
            spec = spec.and(PropertySpecification.hasRoad(road));
        }

        if (minPrice != null && maxPrice != null) {
            spec = spec.and(PropertySpecification.priceBetween(minPrice, maxPrice));
        }

        if (features != null && features.length > 0) {
            spec = spec.and(PropertySpecification.hasFeatures(features));
        }

        if (facility != null && facility.length > 0) {
            spec = spec.and(PropertySpecification.hasFacilities(facility));
        }

        // 查詢符合條件的房源，並返回 Page<Property>
        Page<Property> filteredPropertiesPage = propertyRepository.findAll(spec, pageable);

        // 將 Property 轉換為 PropertyDTO
        return filteredPropertiesPage.map(propertyMapper::PropertyConvertToDTO);
    }
}

