package tw.school.rental_backend.repository.jpa.property;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tw.school.rental_backend.model.property.Property;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByIdIn(List<Long> id);

    List<Property> findTop12ByOrderByCreatedAtDesc();

    Page<Property> findAll(Specification<Property> spec, Pageable pageable);
}


