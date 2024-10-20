package tw.school.rental_backend.repository.dynamo;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import tw.school.rental_backend.model.user.UserAction;

@Repository
@Log4j2
public class UserActionRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public UserActionRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
    }

    public void save(UserAction userAction) {
        try {
            DynamoDbTable<UserAction> userActionTable = dynamoDbEnhancedClient.table("UserActions", TableSchema.fromBean(UserAction.class));
            userActionTable.putItem(userAction);
            log.info("Successfully saved UserAction: {}", userAction);
        } catch (Exception e) {
            log.error("Error saving UserAction: {}", e.getMessage(), e);
            throw e;
        }
    }
}
