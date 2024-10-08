package tw.school.rental_backend.service.Impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.school.rental_backend.data.dto.PropertyDTO;
import tw.school.rental_backend.data.dto.PropertyResponseDTO;
import tw.school.rental_backend.mapper.PropertyMapper;
import tw.school.rental_backend.model.RecommendedProperty;
import tw.school.rental_backend.model.property.Property;
import tw.school.rental_backend.repository.jpa.property.PropertyRepository;
import tw.school.rental_backend.repository.jpa.property.RecommendedPropertyRepository;
import tw.school.rental_backend.service.RecommendationService;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendedPropertyRepository recommendedPropertyRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;

    public RecommendationServiceImpl(RecommendedPropertyRepository recommendedPropertyRepository, PropertyRepository propertyRepository, PropertyMapper propertyMapper) {
        this.recommendedPropertyRepository = recommendedPropertyRepository;
        this.propertyRepository = propertyRepository;
        this.propertyMapper = propertyMapper;
    }

    @Override
    public PropertyResponseDTO<List<PropertyDTO>> recommendPropertyForUser(Long userId, Pageable pageable) {

        Page<RecommendedProperty> recommendedPropertiesPage = recommendedPropertyRepository.findByUserIdOrderByScoreDesc(userId, pageable);

        // 使用者沒有操作記錄，先回傳最新的12筆房源
        if (recommendedPropertiesPage.getTotalElements() == 0) {
            List<Property> latestProperties = propertyRepository.findTop12ByOrderByCreatedAtDesc();
            List<PropertyDTO> latestPropertyDTOs = latestProperties.stream()
                    .map(propertyMapper::PropertyConvertToDTO)
                    .collect(Collectors.toList());

            return new PropertyResponseDTO<>(latestPropertyDTOs);
        }

        // 查詢推薦房源的詳細信息
        List<Long> propertyIds = recommendedPropertiesPage.getContent().stream()
                .map(RecommendedProperty::getPropertyId)
                .collect(Collectors.toList());

        List<Property> properties = propertyRepository.findByIdIn(propertyIds);

        List<PropertyDTO> propertyDTOs = properties.stream()
                .map(propertyMapper::PropertyConvertToDTO)
                .collect(Collectors.toList());

        // 如果推薦房源不足12個，補充最新的房源
        if (propertyDTOs.size() < 12) {
            List<Property> additionalProperties = propertyRepository
                    .findTop12ByOrderByCreatedAtDesc();
            additionalProperties.forEach(property -> {
                PropertyDTO dto = propertyMapper.PropertyConvertToDTO(property);
                if (!propertyDTOs.contains(dto)) {
                    propertyDTOs.add(dto);
                }
            });
        }
        return new PropertyResponseDTO<>(propertyDTOs);
    }
}

