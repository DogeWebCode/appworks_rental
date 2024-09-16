package tw.school.rental_backend.service;

import tw.school.rental_backend.model.property.Property;

import java.util.List;

public interface RecommendationService {

    List<Property> recommendPropertyForUser(Long userId);
}
