package tw.school.rental_backend.service.Impl;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
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
import tw.school.rental_backend.service.PropertyService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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


    public PropertyServiceImpl(PropertyRepository propertyRepository, PropertyMapper propertyMapper, PropertyDetailMapper propertyDetailMapper, UserRepository userRepository, CityRepository cityRepository, DistrictRepository districtRepository, RoadRepository roadRepository, FeatureRepository featureRepository, FacilityRepository facilityRepository, PropertyLayoutRepository propertyLayoutRepository, StorageService storageService) {
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

    @Override
    @Transactional
    public PropertyDetailDTO getPropertyDetail(Long propertyId) {
        Property property = propertyRepository.findById(propertyId).orElseThrow(() -> new RuntimeException("房源不存在"));
        return propertyDetailMapper.PropertyConvertToDetailDTO(property);
    }


    @Transactional
    @Override
    public void createProperty(PropertyForm propertyForm) {
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
        if (propertyForm.getLessor() == null || propertyForm.getLessor().isEmpty()) {
            property.setLessor("Unknown");
        } else {
            property.setLessor(propertyForm.getLessor());
        }
        property.setFloor(propertyForm.getFloor());
        property.setTotalFloor(propertyForm.getTotalFloor());
        if (propertyForm.getStatus() == null || propertyForm.getStatus().isEmpty()) {
            property.setStatus("上架中");
        } else {
            property.setStatus(propertyForm.getStatus());
        }

        // TODO:這裏之後要換成串 GoogleAPI 拿回經緯度
        if (propertyForm.getLatitude() == null) {
            property.setLatitude(BigDecimal.valueOf(22.55));
        } else {
            property.setLatitude(propertyForm.getLatitude());
        }

        // TODO:這裏之後要換成串 GoogleAPI 拿回經緯度
        if (propertyForm.getLongitude() == null) {
            property.setLongitude(BigDecimal.valueOf(121.81));
        } else {
            property.setLongitude(propertyForm.getLongitude());
        }

        // 設置房東
        User user = userRepository.findById(propertyForm.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        property.setUser(user);

        // 設置地區
        City city = cityRepository.findByCityName(propertyForm.getCityName())
                .orElseThrow(() -> new RuntimeException("City not found"));
        District district = districtRepository.findByDistrictName(propertyForm.getDistrictName())
                .orElseThrow(() -> new RuntimeException("District not found"));
        Road road = roadRepository.findByRoadName(propertyForm.getRoadName())
                .orElseThrow(() -> new RuntimeException("Road not found"));
        property.setCity(city);
        property.setDistrict(district);
        property.setRoad(road);
        property.setAddress(propertyForm.getAddress());

        // 上傳主圖片
        String mainImageUrl = storageService.uploadFile(propertyForm.getMainImage(), "images/");
        property.setMainImage(mainImageUrl);

        property.setCreatedAt(LocalDateTime.now());
        property.setModifiedTime(LocalDateTime.now());

        // 生成 ID
        propertyRepository.save(property);

        // 設置房屋佈局
        PropertyLayout propertyLayout = new PropertyLayout();
        propertyLayout.setProperty(property);
        propertyLayout.setRoomCount(propertyForm.getRoomCount());
        propertyLayout.setLivingRoomCount(propertyForm.getLivingRoomCount());
        propertyLayout.setBathroomCount(propertyForm.getBathroomCount());
        propertyLayout.setBalconyCount(propertyForm.getBalconyCount());
        propertyLayout.setKitchenCount(propertyForm.getKitchenCount());

        propertyLayoutRepository.save(propertyLayout);

        // 保存特色
        List<PropertyFeature> features = propertyForm.getFeatures().stream()
                .map(featureName -> {
                    Feature feature = featureRepository.findByFeatureName(featureName)
                            .orElseThrow(() -> new RuntimeException("Feature not found"));
                    return new PropertyFeature(property, feature);
                }).collect(Collectors.toList());
        property.setFeature(features);

        // 保存設備
        List<PropertyFacility> facilities = propertyForm.getFacilities().stream()
                .map(facilityName -> {
                    Facility facility = facilityRepository.findByFacilityName(facilityName)
                            .orElseThrow(() -> new RuntimeException("Facility not found"));
                    return new PropertyFacility(property, facility);
                }).collect(Collectors.toList());
        property.setFacility(facilities);

        // 保存圖片
        List<PropertyImage> images = propertyForm.getImages().stream()
                .map(image -> {
                    String imageUrl = storageService.uploadFile(image, "images/");
                    log.info("Image URL: " + imageUrl);
                    if (imageUrl == null || imageUrl.isEmpty()) {
                        throw new RuntimeException("Failed to upload image, URL is null or empty");
                    }
                    PropertyImage propertyImage = new PropertyImage(property, imageUrl);
                    propertyImage.setCreatedAt(LocalDateTime.now());
                    propertyImage.setModifiedTime(LocalDateTime.now());
                    return propertyImage;
                }).collect(Collectors.toList());
        property.setImage(images);

        // 設置時間戳
        property.setCreatedAt(LocalDateTime.now());
        property.setModifiedTime(LocalDateTime.now());

        // 保存 property 到資料庫
        propertyRepository.save(property);
    }

    @Override
    public Property getPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("找不到房源"));
    }
}

