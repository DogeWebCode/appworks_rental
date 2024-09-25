package tw.school.rental_backend.repository.jpa.geo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.school.rental_backend.model.geo.City;
import tw.school.rental_backend.model.geo.District;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    Optional<District> findByDistrictName(String DistrictName);

    Optional<District> findByDistrictNameAndCity(String districtName, City city);

    List<District> findByCity(City city);
}
