package tw.school.rental_backend.service.Impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tw.school.rental_backend.data.dto.LatLngDTO;
import tw.school.rental_backend.data.dto.GeocodingResponseDTO;
import tw.school.rental_backend.service.GeocodingService;

import java.util.Objects;
import java.util.Optional;

@Service
@Log4j2
public class GeocodingServiceImpl implements GeocodingService {

    @Value("${google.api.key}")
    private String googleApiKey;

    @Value("${google.api.geocoding.url}")
    private String googleGeocodingUrl;

    private final RestTemplate restTemplate;

    public GeocodingServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public Optional<LatLngDTO> getLatLng(String address) {
        String url = googleGeocodingUrl + "?address=" + address + "&key=" + googleApiKey;

        log.info("Sending request to Google Geocoding API: {}", url);

        // 發送請求
        ResponseEntity<GeocodingResponseDTO> response = restTemplate.getForEntity(url, GeocodingResponseDTO.class);

        log.info("完整的API響應 JSON: {}", Objects.requireNonNull(response.getBody()).toString());

        // 檢查 API 回應
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            GeocodingResponseDTO geocodingResponseDTO = response.getBody();

            // 如果結果不為空，返回經緯度
            if (!geocodingResponseDTO.getResults().isEmpty()) {
                GeocodingResponseDTO.Result result = geocodingResponseDTO.getResults().get(0);
                GeocodingResponseDTO.Result.Geometry.Location location = result.getGeometry().getLocation();
                log.info("Successfully retrieved LatLng: {}, {}", location.getLat(), location.getLng());
                return Optional.of(new LatLngDTO(location.getLat(), location.getLng()));
            } else {
                log.error("Geocoding API returned empty results for address: {}", address);
            }
        } else {
            log.error("Failed to get response from Geocoding API. Status code: {}", response.getStatusCode());
            log.error("Response body: {}", response.getBody());
        }

        // 如果無法取得結果，返回空
        return Optional.empty();
    }

}
