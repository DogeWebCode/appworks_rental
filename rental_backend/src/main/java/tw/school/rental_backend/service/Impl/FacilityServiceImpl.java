package tw.school.rental_backend.service.Impl;

import org.springframework.stereotype.Service;
import tw.school.rental_backend.data.dto.FacilityDTO;
import tw.school.rental_backend.model.property.facility.Facility;
import tw.school.rental_backend.repository.jpa.property.FacilityRepository;
import tw.school.rental_backend.service.FacilityService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;

    public FacilityServiceImpl(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @Override
    public List<FacilityDTO> findAllFacilities() {
        List<Facility> facilities = facilityRepository.findAll();
        String imageUrl = "https://d12sfdsmuxoz1g.cloudfront.net/facility_icon/";
        return facilities.stream()
                .map(facility -> new FacilityDTO(
                        facility.getId(),
                        facility.getFacilityName(),
                        imageUrl + facility.getIconUrl()
                ))
                .collect(Collectors.toList());
    }
}
