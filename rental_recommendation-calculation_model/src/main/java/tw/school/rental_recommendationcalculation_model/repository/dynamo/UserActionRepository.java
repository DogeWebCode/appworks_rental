package tw.school.rental_recommendationcalculation_model.repository.dynamo;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import tw.school.rental_recommendationcalculation_model.model.UserAction;

import java.util.ArrayList;
import java.util.List;

@Repository
@Log4j2
public class UserActionRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public UserActionRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
    }

    // 查詢並返回所有的 UserAction
    public List<UserAction> findAllUserActions() {
        try {
            // 獲取 UserActions 表
            DynamoDbTable<UserAction> userActionTable = dynamoDbEnhancedClient.table("UserActions", TableSchema.fromBean(UserAction.class));

            // 創建一個掃描請求
            ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder().build();

            // 執行掃描操作並獲取結果
            List<UserAction> userActions = new ArrayList<>();
            userActionTable.scan(scanRequest).items().forEach(userActions::add);

            log.info("Successfully scanned all UserActions, total: {}", userActions.size());
            return userActions;

        } catch (Exception e) {
            log.error("Error scanning UserActions: {}", e.getMessage(), e);
            throw e;
        }
    }
}
