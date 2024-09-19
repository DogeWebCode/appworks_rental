package tw.school.rental_backend.repository.jpa.property;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.school.rental_backend.model.location.Road;

import java.util.Optional;

public interface RoadRepository extends JpaRepository<Road, Long> {
    Optional<Road> findByRoadName(String roadName);
}
