package tw.school.rental_backend.service.Impl;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import tw.school.rental_backend.model.property.Property;
import tw.school.rental_backend.model.property.facility.Facility;
import tw.school.rental_backend.model.property.facility.PropertyFacility;
import tw.school.rental_backend.model.property.feature.Feature;
import tw.school.rental_backend.model.property.feature.PropertyFeature;

public class PropertySpecification {

    public static Specification<Property> hasCity(String cityName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("city").get("cityName"), cityName);
    }

    public static Specification<Property> hasDistrict(String districtName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("district").get("districtName"), districtName);
    }

    public static Specification<Property> hasRoad(String roadName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("road").get("roadName"), roadName);
    }

    public static Specification<Property> priceBetween(Integer minPrice, Integer maxPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
    }

    public static Specification<Property> hasFeatures(String[] featureNames) {
        return (root, query, criteriaBuilder) -> {
            Join<Property, PropertyFeature> featureJoin = root.join("feature");
            Join<PropertyFeature, Feature> actualFeatureJoin = featureJoin.join("feature");

            return actualFeatureJoin.get("featureName").in((Object[]) featureNames);
        };
    }

    public static Specification<Property> hasFacilities(String[] facilityNames) {
        return (root, query, criteriaBuilder) -> {
            Join<Property, PropertyFacility> facilityJoin = root.join("facility");
            Join<PropertyFacility, Facility> actualFacilityJoin = facilityJoin.join("facility");

            return actualFacilityJoin.get("facilityName").in((Object[]) facilityNames);
        };
    }
}
