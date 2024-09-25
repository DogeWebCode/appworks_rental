package tw.school.rental_backend.repository.jpa.geo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.school.rental_backend.model.geo.District;
import tw.school.rental_backend.model.geo.Road;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoadRepository extends JpaRepository<Road, Long> {
    Optional<Road> findByRoadName(String roadName);

    List<Road> findByDistrict(District district);
}
