package tw.school.rental_backend.data.dto;

import lombok.Data;

@Data
public class DataResponseDTO<T> {
    private T data;

    public DataResponseDTO(T data) {
        this.data = data;
    }
}
