package tw.school.rental_backend.mapper;

import org.springframework.stereotype.Component;
import tw.school.rental_backend.data.dto.LandlordInfoDTO;
import tw.school.rental_backend.data.dto.PropertyDetailDTO;
import tw.school.rental_backend.data.dto.PropertyLayoutDTO;
import tw.school.rental_backend.model.property.Property;
import tw.school.rental_backend.model.property.PropertyLayout;
import tw.school.rental_backend.repository.jpa.property.PropertyLayoutRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PropertyDetailMapper {

    private final PropertyLayoutRepository propertyLayoutRepository;

    public PropertyDetailMapper(PropertyLayoutRepository propertyLayoutRepository) {
        this.propertyLayoutRepository = propertyLayoutRepository;
    }

    public PropertyDetailDTO PropertyConvertToDetailDTO(Property property) {

        String imageUrl = "https://d12sfdsmuxoz1g.cloudfront.net/images/";

        PropertyDetailDTO detailDTO = new PropertyDetailDTO();
        detailDTO.setId(property.getId());
        detailDTO.setUserId(property.getUser().getId());
        detailDTO.setTitle(property.getTitle());
        detailDTO.setCityName(property.getCity().getCityName());
        detailDTO.setDistrictName(property.getDistrict().getDistrictName());
        detailDTO.setRoadName(property.getRoad().getRoadName());
        detailDTO.setAddress(property.getAddress());
        detailDTO.setPrice(property.getPrice());
        detailDTO.setPropertyType(property.getPropertyType());
        detailDTO.setBuildingType(property.getBuildingType());
        detailDTO.setArea(property.getArea());
        detailDTO.setFloor(property.getFloor());
        detailDTO.setStatus(property.getStatus());
        detailDTO.setMainImage(imageUrl + property.getMainImage());
        detailDTO.setCreatedAt(property.getCreatedAt());
        detailDTO.setDescription(property.getDescription());
        detailDTO.setTotalFloor(property.getTotalFloor());
        detailDTO.setDeposit(property.getDeposit());
        detailDTO.setManagementFee(property.getManagementFee());
        detailDTO.setRentPeriod(property.getRentPeriod());
        detailDTO.setLatitude(property.getLatitude());
        detailDTO.setLongitude(property.getLongitude());

        // 查詢 PropertyLayout 並設置到 DTO 中
        PropertyLayout propertyLayout = propertyLayoutRepository.findByProperty(property);
        if (propertyLayout != null) {
            PropertyLayoutDTO layoutDTO = new PropertyLayoutDTO();
            layoutDTO.setRoomCount(propertyLayout.getRoomCount());
            layoutDTO.setLivingRoomCount(propertyLayout.getLivingRoomCount());
            layoutDTO.setBathroomCount(propertyLayout.getBathroomCount());
            layoutDTO.setBalconyCount(propertyLayout.getBalconyCount());
            layoutDTO.setKitchenCount(propertyLayout.getKitchenCount());

            detailDTO.setPropertyLayout(layoutDTO);
        }

        // 設置特色
        List<String> features = property.getFeature().stream()
                .map(propertyFeature -> propertyFeature.getFeature().getFeatureName())
                .collect(Collectors.toList());
        detailDTO.setFeatures(features);

        // 設置設備
        List<String> facilities = property.getFacility().stream()
                .map(facility -> facility.getFacility().getFacilityName())
                .collect(Collectors.toList());
        detailDTO.setFacility(facilities);

        List<String> imageUrls = property.getImage().stream()
                .map(image -> imageUrl + image.getImageUrl())
                .collect(Collectors.toList());
        detailDTO.setImages(imageUrls);

        // 房東資料
        LandlordInfoDTO landlordInfo = new LandlordInfoDTO();
        landlordInfo.setId(property.getUser().getId());
        landlordInfo.setUsername(property.getUser().getUsername());
        landlordInfo.setRole(property.getUser().getRole());
        landlordInfo.setMobilePhone(property.getUser().getMobilePhone());
        landlordInfo.setPicture(property.getUser().getPicture());

        detailDTO.setLandlordInfo(landlordInfo);

        return detailDTO;
    }
}
