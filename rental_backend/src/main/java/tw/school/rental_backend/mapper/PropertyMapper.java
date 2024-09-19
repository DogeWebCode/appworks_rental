package tw.school.rental_backend.mapper;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tw.school.rental_backend.data.dto.PropertyDTO;
import tw.school.rental_backend.data.dto.PropertyLayoutDTO;
import tw.school.rental_backend.model.property.Property;
import tw.school.rental_backend.model.property.PropertyLayout;
import tw.school.rental_backend.repository.jpa.property.PropertyLayoutRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PropertyMapper {

    private final PropertyLayoutRepository propertyLayoutRepository;

    public PropertyMapper(PropertyLayoutRepository propertyLayoutRepository) {
        this.propertyLayoutRepository = propertyLayoutRepository;
    }

    @Transactional
    public PropertyDTO PropertyConvertToDTO(Property property) {
        PropertyDTO propertyDTO = new PropertyDTO();
        propertyDTO.setId(property.getId());
        propertyDTO.setTitle(property.getTitle());
        propertyDTO.setCityName(property.getCity().getCityName());
        propertyDTO.setDistrictName(property.getDistrict().getDistrictName());
        propertyDTO.setRoadName(property.getRoad().getRoadName());
        propertyDTO.setAddress(property.getAddress());
        propertyDTO.setPrice(property.getPrice());
        propertyDTO.setPropertyType(property.getPropertyType());
        propertyDTO.setBuildingType(property.getBuildingType());
        propertyDTO.setArea(property.getArea());
        propertyDTO.setFloor(property.getFloor());
        propertyDTO.setStatus(property.getStatus());

        String imageUrl = "https://d12sfdsmuxoz1g.cloudfront.net/images/";
        propertyDTO.setMainImage(imageUrl + property.getMainImage());
        propertyDTO.setCreatedAt(property.getCreatedAt());

        // 查詢 PropertyLayout 並設置到 DTO 中
        PropertyLayout propertyLayout = propertyLayoutRepository.findByProperty(property);
        if (propertyLayout != null) {
            PropertyLayoutDTO layoutDTO = new PropertyLayoutDTO();
            layoutDTO.setLivingRoomCount(propertyLayout.getLivingRoomCount());
            layoutDTO.setBathroomCount(propertyLayout.getBathroomCount());
            layoutDTO.setBalconyCount(propertyLayout.getBalconyCount());
            layoutDTO.setKitchenCount(propertyLayout.getKitchenCount());

            propertyDTO.setPropertyLayout(layoutDTO);
        }

        List<String> features = property.getFeature().stream()
                .map(propertyFeature -> propertyFeature.getFeature().getFeatureName())
                .collect(Collectors.toList());
        propertyDTO.setFeatures(features);

        return propertyDTO;
    }
}
