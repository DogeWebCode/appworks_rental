package tw.school.rental_backend.repository.jpa.property;

import org.springframework.data.jpa.repository.JpaRepository;
import tw.school.rental_backend.model.property.facility.Facility;

import java.util.Optional;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
    Optional<Facility> findByFacilityName(String facilityName);
}
