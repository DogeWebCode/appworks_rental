package tw.school.rental_backend.service.Impl;

import org.springframework.stereotype.Service;
import tw.school.rental_backend.model.property.feature.Feature;
import tw.school.rental_backend.repository.jpa.property.FeatureRepository;
import tw.school.rental_backend.service.FeatureService;

import java.util.List;

@Service
public class FeatureServiceImpl implements FeatureService {
    private final FeatureRepository featureRepository;

    public FeatureServiceImpl(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    @Override
    public List<Feature> findAllFeatures() {
        return featureRepository.findAll();
    }
}
