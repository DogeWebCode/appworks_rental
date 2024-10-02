package tw.school.rental_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.school.rental_backend.data.dto.DataResponseDTO;
import tw.school.rental_backend.data.dto.DistrictDTO;
import tw.school.rental_backend.data.dto.RoadDTO;
import tw.school.rental_backend.model.geo.City;
import tw.school.rental_backend.service.GeoService;

import java.util.List;

@RestController
@RequestMapping("/api/geo")
public class GeoController {

    private final GeoService geoService;

    public GeoController(GeoService geoService) {
        this.geoService = geoService;
    }

    @GetMapping("/city")
    public ResponseEntity<?> getCity() {
        List<City> city = geoService.findAllCities();
        DataResponseDTO<List<City>> response = new DataResponseDTO<>(city);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/district")
    public ResponseEntity<?> getDistrict(@RequestParam String cityName) {
        List<DistrictDTO> districtDTO = geoService.findDistrictsByCity(cityName);
        DataResponseDTO<List<DistrictDTO>> response = new DataResponseDTO<>(districtDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/road")
    public ResponseEntity<?> getRoad(@RequestParam String districtName, @RequestParam String cityName) {
        List<RoadDTO> roadDTO = geoService.findRoadsByDistrict(districtName, cityName);
        DataResponseDTO<List<RoadDTO>> response = new DataResponseDTO<>(roadDTO);
        return ResponseEntity.ok(response);
    }
}
