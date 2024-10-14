package tw.school.rental_backend.repository.jpa.geo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.school.rental_backend.model.geo.City;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByCityName(String cityName);
}
