package tw.school.rental_backend.repository.jpa.property;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.school.rental_backend.model.property.Property;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    // Find properties by city
    List<Property> findByCityId(Long cityId);

    // Find properties by district
    List<Property> findByDistrictId(Long districtId);

    // Find properties by price range
    List<Property> findByPriceBetween(int minPrice, int maxPrice);

    // Find properties by area
    List<Property> findByAreaBetween(BigDecimal minArea, BigDecimal maxArea);

    // Find properties by property type
    List<Property> findByPropertyType(String propertyType);

    // Find properties by building type
    List<Property> findByBuildingType(String buildingType);

    // Find properties by status (e.g., available, rented)
    List<Property> findByStatus(String status);

    // Find properties by user (landlord)
    List<Property> findByUserId(Long userId);

    List<Property> findByIdIn(List<Long> id);
}


