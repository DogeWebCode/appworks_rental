package tw.school.rental_backend.data.dto;

import lombok.Data;

import java.util.List;

@Data
public class GeocodingResponseDTO {
    private List<Result> results;

    @Data
    public static class Result {
        private Geometry geometry;

        @Data
        public static class Geometry {
            private Location location;

            @Data
            public static class Location {
                private double lat;
                private double lng;
            }
        }
    }
}
