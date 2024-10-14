package tw.school.rental_backend.repository.jpa.property;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.school.rental_backend.model.property.feature.Feature;

import java.util.Optional;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {

    Optional<Feature> findByFeatureName(String featureName);
}
