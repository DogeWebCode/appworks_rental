package tw.school.rental_backend.service.Impl;

import org.springframework.stereotype.Service;
import tw.school.rental_backend.model.property.facility.Facility;
import tw.school.rental_backend.repository.jpa.property.FacilityRepository;
import tw.school.rental_backend.service.FacilityService;

import java.util.List;

@Service
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;

    public FacilityServiceImpl(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @Override
    public List<Facility> findAllFacilities() {
        return facilityRepository.findAll();
    }

}
