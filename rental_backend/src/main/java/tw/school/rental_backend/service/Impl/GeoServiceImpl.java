package tw.school.rental_backend.service.Impl;

import org.springframework.stereotype.Service;
import tw.school.rental_backend.data.dto.DistrictDTO;
import tw.school.rental_backend.data.dto.RoadDTO;
import tw.school.rental_backend.model.geo.City;
import tw.school.rental_backend.model.geo.District;
import tw.school.rental_backend.model.geo.Road;
import tw.school.rental_backend.repository.jpa.geo.CityRepository;
import tw.school.rental_backend.repository.jpa.geo.DistrictRepository;
import tw.school.rental_backend.repository.jpa.geo.RoadRepository;
import tw.school.rental_backend.service.GeoService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeoServiceImpl implements GeoService {

    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    private final RoadRepository roadRepository;

    public GeoServiceImpl(CityRepository cityRepository, DistrictRepository districtRepository, RoadRepository roadRepository) {
        this.cityRepository = cityRepository;
        this.districtRepository = districtRepository;
        this.roadRepository = roadRepository;
    }

    @Override
    public List<City> findAllCities() {
        return cityRepository.findAll();
    }

    @Override
    public List<DistrictDTO> findDistrictsByCity(String cityName) {
        City city = cityRepository.findByCityName(cityName)
                .orElseThrow(() -> new RuntimeException("查無此城市"));
        List<District> districts = districtRepository.findByCity(city);
        return districts.stream()
                .map(district -> new DistrictDTO(district.getId(), district.getDistrictName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RoadDTO> findRoadsByDistrict(String districtName) {
        District district = districtRepository.findByDistrictName(districtName)
                .orElseThrow(() -> new RuntimeException("查無此區域"));
        List<Road> roads = roadRepository.findByDistrict(district);
        return roads.stream()
                .map(road -> new RoadDTO(road.getId(), road.getRoadName()))
                .collect(Collectors.toList());
    }
}

