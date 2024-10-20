package tw.school.rental_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import tw.school.rental_backend.data.dto.LatLngDTO;
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
import tw.school.rental_backend.model.property.feature.Feature;
import tw.school.rental_backend.model.user.User;
import tw.school.rental_backend.repository.jpa.geo.CityRepository;
import tw.school.rental_backend.repository.jpa.geo.DistrictRepository;
import tw.school.rental_backend.repository.jpa.geo.RoadRepository;
import tw.school.rental_backend.repository.jpa.property.FacilityRepository;
import tw.school.rental_backend.repository.jpa.property.FeatureRepository;
import tw.school.rental_backend.repository.jpa.property.PropertyLayoutRepository;
import tw.school.rental_backend.repository.jpa.property.PropertyRepository;
import tw.school.rental_backend.repository.jpa.user.UserRepository;
import tw.school.rental_backend.service.GeocodingService;
import tw.school.rental_backend.service.Impl.PropertyServiceImpl;
import tw.school.rental_backend.service.StorageService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PropertyServiceImplTest {

    @InjectMocks
    private PropertyServiceImpl propertyService;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PropertyDetailMapper propertyDetailMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PropertyMapper propertyMapper;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private DistrictRepository districtRepository;

    @Mock
    private RoadRepository roadRepository;

    @Mock
    private FeatureRepository featureRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private PropertyLayoutRepository propertyLayoutRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private GeocodingService geocodingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateProperty_Success() {
        // 準備測試資料
        PropertyForm propertyForm = new PropertyForm();
        propertyForm.setTitle("台中市北區一中街超讚房源");
        propertyForm.setDescription("");
        propertyForm.setPrice(15000);
        propertyForm.setDeposit(2000);
        propertyForm.setManagementFee(500);
        propertyForm.setRentPeriod("12");
        propertyForm.setPropertyType("獨立套房");
        propertyForm.setBuildingType("透天");
        propertyForm.setArea(new BigDecimal("50.0"));
        propertyForm.setLessor("許阿柴");
        propertyForm.setFloor(5);
        propertyForm.setTotalFloor(10);
        propertyForm.setStatus("上架中");
        propertyForm.setUserId(1L);
        propertyForm.setCityName("臺中市");
        propertyForm.setDistrictName("北區");
        propertyForm.setRoadName("一中街");
        propertyForm.setAddress("10號");
        propertyForm.setLatitude(new BigDecimal("25.0330"));
        propertyForm.setLongitude(new BigDecimal("121.5654"));
        propertyForm.setMainImage(mock(MultipartFile.class));
        propertyForm.setImages(List.of(mock(MultipartFile.class)));
        propertyForm.setFeatures(List.of("Feature1"));
        propertyForm.setFacilities(List.of("Facility1"));
        propertyForm.setRoomCount(2);
        propertyForm.setLivingRoomCount(1);
        propertyForm.setBathroomCount(1);
        propertyForm.setBalconyCount(1);
        propertyForm.setKitchenCount(1);

        // 模擬依賴行為
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(cityRepository.findByCityName("臺中市")).thenReturn(Optional.of(new City()));
        when(districtRepository.findByDistrictNameAndCity(eq("北區"), any(City.class))).thenReturn(Optional.of(new District()));
        when(roadRepository.findByRoadNameAndDistrict(eq("一中街"), any(District.class))).thenReturn(Optional.of(new Road()));
        when(storageService.uploadFile(any(MultipartFile.class), eq("images/"))).thenReturn("http://image.url/main.jpg");
        when(featureRepository.findByFeatureName(anyString())).thenReturn(Optional.of(new Feature()));
        when(facilityRepository.findByFacilityName(anyString())).thenReturn(Optional.of(new Facility()));
        when(geocodingService.getLatLng(anyString())).thenReturn(Optional.of(new LatLngDTO(25.0330, 121.5654)));

        // 執行方法
        propertyService.createProperty(propertyForm);

        // 驗證方法調用
        verify(propertyRepository, times(2)).save(any(Property.class));
        verify(propertyLayoutRepository).save(any(PropertyLayout.class));
        verify(storageService, times(2)).uploadFile(any(MultipartFile.class), eq("images/"));
    }

    @Test
    public void testCreateProperty_UserNotFound() {
        // 準備測試資料
        PropertyForm propertyForm = new PropertyForm();
        propertyForm.setUserId(1L);
        propertyForm.setMainImage(mock(MultipartFile.class)); // 設定 mainImage

        // 模擬 storageService 行為
        when(storageService.uploadFile(any(MultipartFile.class), eq("images/")))
                .thenReturn("http://image.url/main.jpg");

        // 模擬 userRepository return null
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // 執行方法然後抓異常
        Exception exception = assertThrows(RuntimeException.class, () -> {
            propertyService.createProperty(propertyForm);
        });

        // 驗證異常訊息
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testGetPropertyDetail_Success() {
        // 準備測試資料
        Property property = new Property();
        property.setId(1L);
        PropertyDetailDTO detailDTO = new PropertyDetailDTO();

        // 模擬依賴行為
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(propertyDetailMapper.PropertyConvertToDetailDTO(property)).thenReturn(detailDTO);

        // 執行方法
        PropertyDetailDTO result = propertyService.getPropertyDetail(1L);

        // 驗證結果
        assertNotNull(result);
        assertEquals(detailDTO, result);
    }

    @Test
    public void testGetPropertyDetail_NotFound() {
        // 模擬依賴行為
        when(propertyRepository.findById(1L)).thenReturn(Optional.empty());

        // 執行方法然後抓異常
        Exception exception = assertThrows(RuntimeException.class, () -> {
            propertyService.getPropertyDetail(1L);
        });

        // 驗證異常訊息
        assertEquals("房源不存在", exception.getMessage());
    }

    @Test
    public void testFilterProperties() {
        // 準備測試資料
        Page<Property> propertiesPage = mock(Page.class);
        when(propertyRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(propertiesPage);
        when(propertiesPage.map(any())).thenReturn(mock(Page.class));

        // 執行方法
        Page<PropertyDTO> result = propertyService.filterProperties("臺中市", "北區", "一中街", 1000, 2000, new String[]{"Feature1"}, new String[]{"Facility1"}, Pageable.unpaged());

        // 驗證結果
        assertNotNull(result);
        verify(propertyRepository).findAll(any(Specification.class), any(Pageable.class));
    }
}
