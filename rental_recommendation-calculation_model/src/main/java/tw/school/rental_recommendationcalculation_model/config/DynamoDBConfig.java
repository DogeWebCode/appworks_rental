package tw.school.rental_recommendationcalculation_model.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDBConfig {

    @Value("${amazon.aws.region}")
    private String amazonAWSRegion;

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient()).build();
    }

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.of(amazonAWSRegion))  // 使用 AWS 雲端的區域
                .credentialsProvider(InstanceProfileCredentialsProvider.create())  // AWS IAM 權限
                .build();

    }
}