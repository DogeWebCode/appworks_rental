package tw.school.rental_backend.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class PropertyResponseDTO<T> {

    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nextPage;

    private long totalElements;

    private int totalPages;

    public PropertyResponseDTO(T data) {
        this.data = data;
    }
}
