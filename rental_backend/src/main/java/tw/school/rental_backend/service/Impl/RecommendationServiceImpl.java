package tw.school.rental_backend.service.Impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.school.rental_backend.data.dto.PropertyDTO;
import tw.school.rental_backend.data.dto.PropertyResponseDTO;
import tw.school.rental_backend.mapper.PropertyMapper;
import tw.school.rental_backend.model.property.Property;
import tw.school.rental_backend.model.user.UserAction;
import tw.school.rental_backend.repository.jpa.property.PropertyRepository;
import tw.school.rental_backend.repository.jpa.user.UserActionRepository;
import tw.school.rental_backend.service.RecommendationService;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final UserActionRepository userActionRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;

    public RecommendationServiceImpl(UserActionRepository actionRepository, PropertyRepository propertyRepository, PropertyMapper propertyMapper) {
        this.userActionRepository = actionRepository;
        this.propertyRepository = propertyRepository;
        this.propertyMapper = propertyMapper;
    }

    @Override
    public PropertyResponseDTO<List<PropertyDTO>> recommendPropertyForUser(Long userId, Pageable pageable) {
        // 獲取使用者行為記錄
        List<UserAction> userActions = userActionRepository.findByUserId(userId);

        // 如果使用者沒有行為記錄，返回最新的12個房源
        if (userActions.isEmpty()) {
            Page<Property> latestPropertiesPage = propertyRepository.findTop12ByOrderByCreatedAtDesc(pageable);
            List<PropertyDTO> latestPropertyDTOs = latestPropertiesPage.getContent().stream()
                    .map(propertyMapper::PropertyConvertToDTO)
                    .collect(Collectors.toList());

            return new PropertyResponseDTO<>(latestPropertyDTOs);
        }

        // 使用者的行為記錄，累計推薦分數
        Map<Long, Integer> propertyScore = new HashMap<>();
        Map<String, Integer> propertyTypeCount = new HashMap<>();
        Set<String> viewedDistricts = new HashSet<>();
        Set<String> viewedCities = new HashSet<>();
        int avgPrice = 0;

        // 計算加權分數和統計地區與價格
        for (UserAction actionItem : userActions) {
            Property property = actionItem.getProperty();
            Long propertyId = property.getId();
            String propertyType = property.getPropertyType();

            // 統計每種類型的出現次數
            propertyTypeCount.put(propertyType, propertyTypeCount.getOrDefault(propertyType, 0) + 1);
            log.info("Property type count: {}", propertyTypeCount);
            viewedDistricts.add(property.getDistrict().getDistrictName());
            viewedCities.add(property.getCity().getCityName());
            avgPrice += property.getPrice();

            // 基於行為進行加權
            int score = calculateScoreForAction(actionItem);
            propertyScore.put(propertyId, propertyScore.getOrDefault(propertyId, 0) + score);

        }
        log.info("viewedDistricts: {}", viewedDistricts);
        log.info("viewedCities: {}", viewedCities);
        log.info("Property score: {}", propertyScore);

        // 計算平均價格，並設置價格範圍
        avgPrice /= userActions.size();
        log.info("Average price: {}", avgPrice);

        int priceLowerBound = (int) (avgPrice * 0.8);
        int priceUpperBound = (int) (avgPrice * 1.2);
        log.info("Price range: {} - {}", priceLowerBound, priceUpperBound);

        // 查詢符合條件的房源
        Page<Property> candidatePropertiesPage = propertyRepository.findByCityAndDistrictNamesAndPriceBetween(
                viewedCities, viewedDistricts, priceLowerBound, priceUpperBound, pageable);
        List<Property> candidateProperties = candidatePropertiesPage.getContent();

        // 為符合條件的房源加分
        for (Property property : candidateProperties) {
            Long propertyId = property.getId();
            String propertyType = property.getPropertyType();

            // 基於 propertyType 的加權
            int typeWeight = propertyTypeCount.getOrDefault(propertyType, 0);
            propertyScore.put(propertyId, propertyScore.getOrDefault(propertyId, 0) + typeWeight);

            // 基於價格範圍的加權
            if (property.getPrice() >= priceLowerBound && property.getPrice() <= priceUpperBound) {
                propertyScore.put(propertyId, propertyScore.get(propertyId) + 5);
            }
        }

        // 按加權分數排序並篩選出推薦房源ID
        List<Long> sortedPropertyIds = propertyScore.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        // 查詢推薦的房源
        Page<Property> recommendedPropertiesPage = propertyRepository.findByIdIn(sortedPropertyIds, pageable);
        List<Property> recommendedProperties = recommendedPropertiesPage.getContent();

        // 如果推薦結果不足12個，補充房源
        if (recommendedProperties.size() < 12) {
            List<Property> additionalProperties = propertyRepository
                    .findTop12ByPriceBetweenOrderByCreatedAtDesc(priceLowerBound, priceUpperBound, pageable)
                    .getContent();
            for (Property property : additionalProperties) {
                if (!recommendedProperties.contains(property)) {
                    recommendedProperties.add(property);
                }
            }
        }

        // 將推薦結果轉換成 DTO
        List<PropertyDTO> propertyDTOs = recommendedProperties.stream()
                .map(propertyMapper::PropertyConvertToDTO)
                .collect(Collectors.toList());

        return new PropertyResponseDTO<>(propertyDTOs);
    }

    private int calculateScoreForAction(UserAction action) {
        // 根據行為類型進行加權
        return switch (action.getActionType()) {
            case "view" -> 1;
            case "favorite" -> 5;
            case "contact" -> 10;
            default -> 0;
        };
    }
}

