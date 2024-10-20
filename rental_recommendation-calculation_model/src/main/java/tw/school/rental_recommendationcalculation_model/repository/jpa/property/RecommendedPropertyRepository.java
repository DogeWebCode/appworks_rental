package tw.school.rental_recommendationcalculation_model.repository.jpa.property;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.school.rental_recommendationcalculation_model.model.RecommendedProperty;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendedPropertyRepository extends JpaRepository<RecommendedProperty, Long> {
    List<RecommendedProperty> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    Optional<RecommendedProperty> findByUserIdAndPropertyId(Long userId, Long propertyId);
}
