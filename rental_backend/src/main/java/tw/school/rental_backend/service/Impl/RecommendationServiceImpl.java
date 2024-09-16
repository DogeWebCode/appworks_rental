package tw.school.rental_backend.service.Impl;

import org.springframework.stereotype.Service;
import tw.school.rental_backend.model.property.Property;
import tw.school.rental_backend.repository.jpa.property.PropertyRepository;
import tw.school.rental_backend.repository.jpa.user.UserActionRepository;
import tw.school.rental_backend.service.RecommendationService;

import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final UserActionRepository actionRepository;
    private final PropertyRepository propertyRepository;

    public RecommendationServiceImpl(UserActionRepository actionRepository, PropertyRepository propertyRepository) {
        this.actionRepository = actionRepository;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public List<Property> recommendPropertyForUser(Long userId) {
        return List.of();
    }
}
