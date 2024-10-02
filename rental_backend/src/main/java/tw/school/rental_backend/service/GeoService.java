package tw.school.rental_backend.service;

import tw.school.rental_backend.data.dto.DistrictDTO;
import tw.school.rental_backend.data.dto.RoadDTO;
import tw.school.rental_backend.model.geo.City;

import java.util.List;

public interface GeoService {
    List<City> findAllCities();

    List<DistrictDTO> findDistrictsByCity(String cityName);

    List<RoadDTO> findRoadsByDistrict(String districtName, String cityName);
}
