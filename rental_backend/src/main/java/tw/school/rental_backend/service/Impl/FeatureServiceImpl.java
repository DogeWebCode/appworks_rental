package tw.school.rental_backend.service.Impl;

import org.springframework.stereotype.Service;
import tw.school.rental_backend.data.dto.FeatureDTO;
import tw.school.rental_backend.model.property.feature.Feature;
import tw.school.rental_backend.repository.jpa.property.FeatureRepository;
import tw.school.rental_backend.service.FeatureService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeatureServiceImpl implements FeatureService {
    private final FeatureRepository featureRepository;

    public FeatureServiceImpl(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    @Override
    public List<FeatureDTO> findAllFeatures() {
        List<Feature> features = featureRepository.findAll();
        String imageUrl = "https://d12sfdsmuxoz1g.cloudfront.net/feature_icon/";
        return features.stream()
                .map(feature -> new FeatureDTO(
                        feature.getId(),
                        feature.getFeatureName(),
                        imageUrl + feature.getIconUrl()
                ))
                .collect(Collectors.toList());
    }
}
