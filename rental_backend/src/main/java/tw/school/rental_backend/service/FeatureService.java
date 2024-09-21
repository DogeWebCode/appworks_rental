package tw.school.rental_backend.service;

import tw.school.rental_backend.model.property.feature.Feature;

import java.util.List;

public interface FeatureService {
    List<Feature> findAllFeatures();
}
