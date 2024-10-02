package tw.school.rental_backend.service;

import tw.school.rental_backend.data.dto.FacilityDTO;
import tw.school.rental_backend.model.property.facility.Facility;

import java.util.List;

public interface FacilityService {
    List<FacilityDTO> findAllFacilities();
}
