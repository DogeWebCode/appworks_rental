package tw.school.rental_backend.repository.jpa.property;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.school.rental_backend.model.property.Property;
import tw.school.rental_backend.model.property.PropertyLayout;

@Repository
public interface PropertyLayoutRepository extends JpaRepository<PropertyLayout, Long> {

    PropertyLayout findByProperty(Property property);
}
