package tw.school.rental_recommendationcalculation_model.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import tw.school.rental_recommendationcalculation_model.model.UserAction;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserActionService {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public UserActionService(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
    }

    public List<UserAction> getUserActions(Long userId) {
        var table = dynamoDbEnhancedClient.table("UserActions", TableSchema.fromBean(UserAction.class));
        var results = table.query(r -> r.queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(userId.toString()))));

        return results.items().stream().collect(Collectors.toList());
    }

}
