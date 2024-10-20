package tw.school.rental_backend.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class UserAction {

    private String userId;
    private String propertyId;
    private String actionType;
    private Long actionTime;

    // 定義 userId 為 Hash Key（分區鍵）
    @DynamoDbPartitionKey
    public String getUserId() {
        return userId;
    }

    // 定義 actionTime 為 Sort Key（排序鍵）
    @DynamoDbSortKey
    public Long getActionTime() {
        return actionTime;
    }

    // 定義 propertyId 為一個普通屬性
    @DynamoDbAttribute("propertyId")
    public String getPropertyId() {
        return propertyId;
    }

    // 定義 actionType 為一個普通屬性
    @DynamoDbAttribute("actionType")
    public String getActionType() {
        return actionType;
    }

    // 忽略該屬性，不存儲到 DynamoDB
    @DynamoDbIgnore
    public LocalDateTime getActionDateTime() {
        return LocalDateTime.ofEpochSecond(actionTime, 0, ZoneOffset.UTC);
    }

    public void setActionDateTime(LocalDateTime dateTime) {
        this.actionTime = dateTime.toEpochSecond(ZoneOffset.UTC);
    }
}