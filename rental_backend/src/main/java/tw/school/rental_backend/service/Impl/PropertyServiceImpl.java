package tw.school.rental_backend.service.Impl;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tw.school.rental_backend.data.dto.PropertyDTO;
import tw.school.rental_backend.data.dto.PropertyDetailDTO;
import tw.school.rental_backend.data.dto.form.PropertyForm;
import tw.school.rental_backend.mapper.PropertyDetailMapper;
import tw.school.rental_backend.mapper.PropertyMapper;
import tw.school.rental_backend.model.geo.City;
import tw.school.rental_backend.model.geo.District;
import tw.school.rental_backend.model.geo.Road;
import tw.school.rental_backend.model.property.Property;
import tw.school.rental_backend.model.property.PropertyLayout;
import tw.school.rental_backend.model.property.facility.Facility;
import tw.school.rental_backend.model.property.facility.PropertyFacility;
import tw.school.rental_backend.model.property.feature.Feature;
import tw.school.rental_backend.model.property.feature.PropertyFeature;
import tw.school.rental_backend.model.property.image.PropertyImage;
import tw.school.rental_backend.model.user.User;
import tw.school.rental_backend.repository.jpa.geo.CityRepository;
import tw.school.rental_backend.repository.jpa.geo.DistrictRepository;
import tw.school.rental_backend.repository.jpa.geo.RoadRepository;
import tw.school.rental_backend.repository.jpa.property.FacilityRepository;
import tw.school.rental_backend.repository.jpa.property.*;
import tw.school.rental_backend.repository.jpa.user.UserRepository;
import tw.school.rental_backend.service.GeocodingService;
import tw.school.rental_backend.service.PropertyService;
import tw.school.rental_backend.data.dto.LatLngDTO;
import tw.school.rental_backend.service.StorageService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;
    private final PropertyDetailMapper propertyDetailMapper;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    private final RoadRepository roadRepository;
    private final FeatureRepository featureRepository;
    private final FacilityRepository facilityRepository;
    private final PropertyLayoutRepository propertyLayoutRepository;
    private final StorageService storageService;
    private final GeocodingService geocodingService;

    public PropertyServiceImpl(
            PropertyRepository propertyRepository,
            PropertyMapper propertyMapper,
            PropertyDetailMapper propertyDetailMapper,
            UserRepository userRepository,
            CityRepository cityRepository,
            DistrictRepository districtRepository,
            RoadRepository roadRepository,
            FeatureRepository featureRepository,
            FacilityRepository facilityRepository,
            PropertyLayoutRepository propertyLayoutRepository,
            StorageService storageService,
            GeocodingService geocodingService) {

        this.propertyRepository = propertyRepository;
        this.propertyMapper = propertyMapper;
        this.propertyDetailMapper = propertyDetailMapper;
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.districtRepository = districtRepository;
        this.roadRepository = roadRepository;
        this.featureRepository = featureRepository;
        this.facilityRepository = facilityRepository;
        this.propertyLayoutRepository = propertyLayoutRepository;
        this.storageService = storageService;
        this.geocodingService = geocodingService;
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

        Specification<Property> spec = buildSpecification(city, district, road, minPrice, maxPrice, features, facility);

        Page<Property> filteredPropertiesPage = propertyRepository.findAll(spec, pageable);

        return filteredPropertiesPage.map(propertyMapper::PropertyConvertToDTO);
    }

    @Transactional
    @Override
    public PropertyDetailDTO getPropertyDetail(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("房源不存在"));
        return propertyDetailMapper.PropertyConvertToDetailDTO(property);
    }

    @Transactional
    @Override
    public void createProperty(PropertyForm propertyForm) {
        User user = userRepository.findById(propertyForm.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Property property = initializeProperty(propertyForm);
        property.setUser(user);

        setAddressAndGeocode(property, propertyForm);
        saveProperty(property, propertyForm);
    }

    private Property initializeProperty(PropertyForm propertyForm) {
        Property property = new Property();
        property.setTitle(propertyForm.getTitle());
        property.setDescription(propertyForm.getDescription());
        property.setPrice(propertyForm.getPrice());
        property.setDeposit(propertyForm.getDeposit());
        property.setManagementFee(propertyForm.getManagementFee());
        property.setRentPeriod(propertyForm.getRentPeriod());
        property.setPropertyType(propertyForm.getPropertyType());
        property.setBuildingType(propertyForm.getBuildingType());
        property.setArea(propertyForm.getArea());
        property.setLessor(Optional.ofNullable(propertyForm.getLessor()).orElse("Unknown"));
        property.setFloor(propertyForm.getFloor());
        property.setTotalFloor(propertyForm.getTotalFloor());
        property.setStatus(Optional.ofNullable(propertyForm.getStatus()).orElse("上架中"));
        property.setCreatedAt(LocalDateTime.now());
        property.setModifiedTime(LocalDateTime.now());

        String mainImageUrl = uploadMainImage(propertyForm.getMainImage());
        property.setMainImage(mainImageUrl);
        return property;
    }

    private void setAddressAndGeocode(Property property, PropertyForm propertyForm) {
        String fullAddress = propertyForm.getCityName() + propertyForm.getDistrictName() + propertyForm.getRoadName() + propertyForm.getAddress();
        log.info("Address: {}", fullAddress);

        // 使用經緯度
        if (propertyForm.getLatitude() == null || propertyForm.getLongitude() == null) {
            Optional<LatLngDTO> latLng = geocodingService.getLatLng(fullAddress);
            if (latLng.isPresent()) {
                property.setLatitude(BigDecimal.valueOf(latLng.get().getLat()));
                property.setLongitude(BigDecimal.valueOf(latLng.get().getLng()));
            } else {
                throw new RuntimeException("無法通過地址獲取經緯度");
            }
        } else {
            property.setLatitude(propertyForm.getLatitude());
            property.setLongitude(propertyForm.getLongitude());

        }

        setCityDistrictRoad(property, propertyForm);
    }

    private void setCityDistrictRoad(Property property, PropertyForm propertyForm) {
        City city = cityRepository.findByCityName(propertyForm.getCityName())
                .orElseThrow(() -> new RuntimeException("City not found"));
        District district = districtRepository.findByDistrictNameAndCity(propertyForm.getDistrictName(), city)
                .orElseThrow(() -> new RuntimeException("District not found in the city: " + propertyForm.getCityName()));
        Road road = roadRepository.findByRoadNameAndDistrict(propertyForm.getRoadName(), district)
                .orElseThrow(() -> new RuntimeException("Road not found in the district and city"));
        property.setCity(city);
        property.setDistrict(district);
        property.setRoad(road);
        property.setAddress(propertyForm.getAddress());
    }

    private void saveProperty(Property property, PropertyForm propertyForm) {
        propertyRepository.save(property);

        PropertyLayout propertyLayout = createPropertyLayout(property, propertyForm);
        propertyLayoutRepository.save(propertyLayout);

        List<PropertyFeature> features = createPropertyFeatures(property, propertyForm.getFeatures());
        property.setFeature(features);

        List<PropertyFacility> facilities = createPropertyFacilities(property, propertyForm.getFacilities());
        property.setFacility(facilities);

        List<PropertyImage> images = uploadPropertyImages(property, propertyForm.getImages());
        property.setImage(images);

        propertyRepository.save(property);
    }

    private PropertyLayout createPropertyLayout(Property property, PropertyForm propertyForm) {
        PropertyLayout layout = new PropertyLayout();
        layout.setProperty(property);
        layout.setRoomCount(propertyForm.getRoomCount());
        layout.setLivingRoomCount(propertyForm.getLivingRoomCount());
        layout.setBathroomCount(propertyForm.getBathroomCount());
        layout.setBalconyCount(propertyForm.getBalconyCount());
        layout.setKitchenCount(propertyForm.getKitchenCount());
        return layout;
    }

    private List<PropertyFeature> createPropertyFeatures(Property property, List<String> featureNames) {
        return featureNames.stream()
                .map(featureName -> {
                    Feature feature = featureRepository.findByFeatureName(featureName)
                            .orElseThrow(() -> new RuntimeException("Feature not found"));
                    return new PropertyFeature(property, feature);
                })
                .collect(Collectors.toList());
    }

    private List<PropertyFacility> createPropertyFacilities(Property property, List<String> facilityNames) {
        return facilityNames.stream()
                .map(facilityName -> {
                    Facility facility = facilityRepository.findByFacilityName(facilityName)
                            .orElseThrow(() -> new RuntimeException("Facility not found"));
                    return new PropertyFacility(property, facility);
                })
                .collect(Collectors.toList());
    }

    private String uploadMainImage(MultipartFile mainImage) {
        String imageUrl = storageService.uploadFile(mainImage, "images/");
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new RuntimeException("Failed to upload main image, URL is null or empty");
        }
        return imageUrl;
    }

    private List<PropertyImage> uploadPropertyImages(Property property, List<MultipartFile> images) {
        return images.stream()
                .map(image -> {
                    // 上傳文件，並獲取文件的 URL
                    String imageUrl = storageService.uploadFile(image, "images/");

                    // 檢查是否上傳成功
                    if (imageUrl == null || imageUrl.isEmpty()) {
                        throw new RuntimeException("Failed to upload image, URL is null or empty");
                    }

                    PropertyImage propertyImage = new PropertyImage(property, imageUrl);
                    propertyImage.setCreatedAt(LocalDateTime.now());
                    propertyImage.setModifiedTime(LocalDateTime.now());
                    return propertyImage;
                })
                .collect(Collectors.toList());
    }


    private Specification<Property> buildSpecification(
            String city, String district, String road, Integer minPrice, Integer maxPrice, String[] features, String[] facility) {

        Specification<Property> spec = Specification.where(null);

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

        return spec;
    }

    @Override
    public Property getPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("找不到房源"));
    }
}
