package tw.school.rental_recommendationcalculation_model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.school.rental_recommendationcalculation_model.model.RecommendedProperty;
import tw.school.rental_recommendationcalculation_model.repository.jpa.property.RecommendedPropertyRepository;

import java.util.*;

@Service
public class RecommendationHelperService {
    private final RecommendedPropertyRepository recommendedPropertyRepository;

    public RecommendationHelperService(RecommendedPropertyRepository recommendedPropertyRepository) {
        this.recommendedPropertyRepository = recommendedPropertyRepository;
    }

    @Transactional
    public void saveRecommendationResults(Long userId, Map<Long, Integer> propertyScores) {
        // 只保留前12筆推薦
        List<Map.Entry<Long, Integer>> top12Recommendations = propertyScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(12)
                .toList();

        // 查詢該用戶是否已有推薦記錄
        List<RecommendedProperty> existingRecommendations = recommendedPropertyRepository.findByUserId(userId);

        if (!existingRecommendations.isEmpty()) {
            // 刪除現有推薦，然後插入新的推薦
            recommendedPropertyRepository.deleteByUserId(userId);
        }

        // 插入新的推薦記錄
        List<RecommendedProperty> recommendedProperties = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : top12Recommendations) {
            Long propertyId = entry.getKey();
            Integer score = entry.getValue();

            // 檢查推薦記錄是否已存在
            Optional<RecommendedProperty> existingRecommendation = recommendedPropertyRepository.findByUserIdAndPropertyId(userId, propertyId);
            if (existingRecommendation.isEmpty()) {
                RecommendedProperty recommendedProperty = new RecommendedProperty(userId, propertyId, score);
                recommendedProperties.add(recommendedProperty);
            } else {
                // 如果已存在，更新分數
                RecommendedProperty recommendation = existingRecommendation.get();
                recommendation.setScore(score);
                recommendedProperties.add(recommendation);
            }
        }
        recommendedPropertyRepository.saveAll(recommendedProperties);
    }
}

