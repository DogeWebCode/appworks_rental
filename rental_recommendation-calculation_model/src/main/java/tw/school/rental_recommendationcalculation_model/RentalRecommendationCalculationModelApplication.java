package tw.school.rental_recommendationcalculation_model;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import tw.school.rental_recommendationcalculation_model.service.RecommendationService;

@SpringBootApplication
public class RentalRecommendationCalculationModelApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalRecommendationCalculationModelApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            RecommendationService recommendationService = ctx.getBean(RecommendationService.class);

            // 觸發為所有用戶計算推薦
            recommendationService.calculateAndSaveRecommendationsForAllUsers();
        };
    }
}
