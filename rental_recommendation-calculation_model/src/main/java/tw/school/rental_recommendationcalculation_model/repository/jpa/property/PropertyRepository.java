package tw.school.rental_recommendationcalculation_model.repository.jpa.property;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.school.rental_recommendationcalculation_model.model.Property;

import java.util.List;
import java.util.Set;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query("SELECT p FROM Property p WHERE p.city.cityName IN :cityNames AND p.district.districtName IN :districtNames AND p.price BETWEEN :priceLowerBound AND :priceUpperBound")
    List<Property> findByCityAndDistrictNamesAndPriceBetween(@Param("cityNames") Set<String> cityNames,
                                                             @Param("districtNames") Set<String> districtNames,
                                                             @Param("priceLowerBound") int priceLowerBound,
                                                             @Param("priceUpperBound") int priceUpperBound);

}


