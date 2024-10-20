package tw.school.rental_backend.service;

import tw.school.rental_backend.data.dto.LatLngDTO;

import java.util.Optional;

public interface GeocodingService {
    Optional<LatLngDTO> getLatLng(String address);
}
