package tw.school.rental_recommendationcalculation_model.service;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import tw.school.rental_recommendationcalculation_model.model.Property;
import tw.school.rental_recommendationcalculation_model.model.UserAction;
import tw.school.rental_recommendationcalculation_model.repository.dynamo.UserActionRepository;
import tw.school.rental_recommendationcalculation_model.repository.jpa.property.PropertyRepository;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RecommendationService {

    private final UserActionRepository userActionRepository;
    private final PropertyRepository propertyRepository;
    private final RecommendationHelperService recommendationHelperService;
    private final ApplicationContext context;
    private final DynamoDbClient dynamoDbClient;


    public RecommendationService(UserActionRepository userActionRepository, PropertyRepository propertyRepository, RecommendationHelperService recommendationHelperService, ConfigurableApplicationContext context, DynamoDbClient dynamoDbClient) {
        this.userActionRepository = userActionRepository;
        this.propertyRepository = propertyRepository;
        this.recommendationHelperService = recommendationHelperService;
        this.context = context;
        this.dynamoDbClient = dynamoDbClient;
    }

    @Transactional
    public void calculateAndSaveRecommendationsForAllUsers() {
        // 從 DynamoDB 中獲取所有用戶的操作記錄
        List<UserAction> allUserActions = userActionRepository.findAllUserActions();
        if (allUserActions.isEmpty()) {
            log.warn("無任何操作記錄，跳過計算。");
            return;
        }

        // 根據用戶ID將操作記錄分組
        Map<String, List<UserAction>> actionsByUser = allUserActions.stream()
                .collect(Collectors.groupingBy(UserAction::getUserId));

        // 遍歷每個用戶的行為，並計算推薦
        for (Map.Entry<String, List<UserAction>> entry : actionsByUser.entrySet()) {
            String userId = entry.getKey();
            List<UserAction> userActions = entry.getValue();

            log.info("為用戶 {} 計算推薦", userId);

            calculateAndSaveRecommendations(Long.parseLong(userId), userActions);
        }

        // 構建要插入的項目
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("InstanceId", AttributeValue.builder().s("i-002b5bbc5b9f92718").build());
        item.put("Status", AttributeValue.builder().s("completed").build());
        item.put("UpdatedAt", AttributeValue.builder().s(new Date().toString()).build());

        // 創建 PutItem 請求
        PutItemRequest request = PutItemRequest.builder()
                .tableName("EC2Status")
                .item(item)
                .build();

        // 將項目插入 DynamoDB
        dynamoDbClient.putItem(request);

        // 確保所有操作完成後再關閉
        log.info("所有推薦計算已完成，準備關閉程序。");

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                SpringApplication.exit(context, () -> 0);
            } catch (InterruptedException e) {
                log.error("程序等待中斷", e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void calculateAndSaveRecommendations(Long userId, List<UserAction> userActions) {
        Map<Long, Integer> propertyScores = new HashMap<>();
        Set<String> viewedDistricts = new HashSet<>();
        Set<String> viewedCities = new HashSet<>();
        int avgPrice = 0;

        // 根據用戶行為計算推薦分數
        for (UserAction action : userActions) {
            Property property = propertyRepository.findById(Long.parseLong(action.getPropertyId())).orElse(null);
            if (property == null) {
                log.warn("未找到 ID 為 {} 的房源", action.getPropertyId());
                continue;
            }

            Long propertyId = property.getId();
            int score = calculateScoreForAction(action);

            propertyScores.put(propertyId, propertyScores.getOrDefault(propertyId, 0) + score);
            viewedDistricts.add(property.getDistrict().getDistrictName());
            viewedCities.add(property.getCity().getCityName());
            avgPrice += property.getPrice();
        }

        avgPrice /= userActions.size();
        int priceLowerBound = (int) (avgPrice * 0.8);
        int priceUpperBound = (int) (avgPrice * 1.2);

        // 查詢符合條件的房源並加權
        List<Property> candidateProperties = propertyRepository.findByCityAndDistrictNamesAndPriceBetween(
                viewedCities, viewedDistricts, priceLowerBound, priceUpperBound);

        for (Property property : candidateProperties) {
            Long propertyId = property.getId();
            propertyScores.put(propertyId, propertyScores.getOrDefault(propertyId, 0) + 5);
        }

        // 儲存推薦結果
        recommendationHelperService.saveRecommendationResults(userId, propertyScores);
    }

    private int calculateScoreForAction(UserAction action) {
        return switch (action.getActionType()) {
            case "view" -> 1;
            case "favorite" -> 5;
            default -> 0;
        };
    }
}

