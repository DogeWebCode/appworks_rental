package tw.school.rental_backend.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.school.rental_backend.data.dto.UserActionDTO;
import tw.school.rental_backend.model.property.Property;
import tw.school.rental_backend.model.user.User;
import tw.school.rental_backend.model.user.UserAction;
import tw.school.rental_backend.repository.jpa.property.PropertyRepository;
import tw.school.rental_backend.repository.jpa.user.UserActionRepository;
import tw.school.rental_backend.repository.jpa.user.UserRepository;
import tw.school.rental_backend.service.UserActionService;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Service
@Log4j2
public class UserActionServiceImpl implements UserActionService {

    private final UserActionRepository userActionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public UserActionServiceImpl(UserActionRepository userActionRepository, RedisTemplate<String, Object> redisTemplate, UserRepository userRepository, PropertyRepository propertyRepository) {
        this.userActionRepository = userActionRepository;
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
    }

    private static final String USER_ACTION_CACHE_PREFIX = "user_action::";

    // 將使用者操作存入 Redis
    @Override
    public void recordUserAction(User user, Property property, String actionType) {
        String cacheKey = USER_ACTION_CACHE_PREFIX + user.getId() + "::" + property.getId();

        // 創建 DTO，避免將整個 Property 實體存儲到 Redis
        UserActionDTO userActionDTO = new UserActionDTO(user.getId(), property.getId(), actionType, LocalDateTime.now());

        // 確認數據類型並記錄到日誌
        log.info("Storing action in Redis: " + userActionDTO);

        redisTemplate.opsForList().rightPush(cacheKey, userActionDTO);
    }

    // 批次將 Redis 中的操作寫入資料庫
    @Override
    public void batchSaveActions() {
        Set<String> keys = redisTemplate.keys(USER_ACTION_CACHE_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            log.warn("No keys found in Redis with prefix: " + USER_ACTION_CACHE_PREFIX);
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // 用 JavaTimeModule 處理 LocalDateTime

        for (String key : keys) {

            List<Object> actions = redisTemplate.opsForList().range(key, 0, -1);
            if (actions == null || actions.isEmpty()) {
                continue; // 如果沒有行為記錄，則繼續下一個 key
            }

            for (Object actionObj : actions) {
                if (actionObj instanceof LinkedHashMap) {
                    // 使用 ObjectMapper 來將 LinkedHashMap 轉換為 UserActionDTO
                    UserActionDTO actionDTO = objectMapper.convertValue(actionObj, UserActionDTO.class);

                    // 通過查詢加載 User 和 Property
                    User user = userRepository.findById(actionDTO.getUserId())
                            .orElseThrow(() -> new IllegalArgumentException("User not found"));
                    Property property = propertyRepository.findById(actionDTO.getPropertyId())
                            .orElseThrow(() -> new IllegalArgumentException("Property not found"));

                    // 創建 UserAction
                    UserAction userAction = new UserAction();
                    userAction.setUser(user);
                    userAction.setProperty(property);
                    userAction.setActionType(actionDTO.getActionType());
                    userAction.setActionTime(actionDTO.getActionTime());

                    userActionRepository.save(userAction);
                } else {
                    log.warn("Unexpected object type in Redis for key: " + key);
                }
            }
            log.info("Deleting key from Redis: " + key);
            // 刪除 Redis 中已處理的鍵
            redisTemplate.delete(key);
        }
    }


    // 使用者取消收藏時要刪除
    @Override
    @Transactional
    public void removeFavoriteAction(Long userId, Long propertyId) {
        userActionRepository.deleteByUserIdAndPropertyIdAndActionType(userId, propertyId, "favorite");
    }
}
