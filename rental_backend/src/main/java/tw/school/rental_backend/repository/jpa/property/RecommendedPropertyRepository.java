package tw.school.rental_backend.repository.jpa.property;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tw.school.rental_backend.model.RecommendedProperty;

public interface RecommendedPropertyRepository extends JpaRepository<RecommendedProperty, Long> {
    Page<RecommendedProperty> findByUserIdOrderByScoreDesc(Long userId, Pageable pageable);
}
