package tw.school.rental_backend.repository.jpa.property;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tw.school.rental_backend.model.property.Property;

import java.util.List;
import java.util.Set;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByIdIn(List<Long> id);

    List<Property> findTop12ByOrderByCreatedAtDesc();

    Page<Property> findTop12ByPriceBetweenOrderByCreatedAtDesc(int priceLowerBound, int priceUpperBound, Pageable pageable);

    @Query("SELECT p FROM Property p WHERE p.city.cityName IN :cityNames AND p.district.districtName IN :districtNames AND p.price BETWEEN :priceLowerBound AND :priceUpperBound")
    Page<Property> findByCityAndDistrictNamesAndPriceBetween(@Param("cityNames") Set<String> cityNames,
                                                             @Param("districtNames") Set<String> districtNames,
                                                             @Param("priceLowerBound") int priceLowerBound,
                                                             @Param("priceUpperBound") int priceUpperBound,
                                                             Pageable pageable);

    Page<Property> findAll(Specification<Property> spec, Pageable pageable);
}


