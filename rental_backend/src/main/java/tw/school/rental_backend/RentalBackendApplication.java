package tw.school.rental_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RentalBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalBackendApplication.class, args);
    }

}
