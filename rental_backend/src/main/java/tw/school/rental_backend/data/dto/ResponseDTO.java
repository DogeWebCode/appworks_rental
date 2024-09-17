package tw.school.rental_backend.data.dto;

import lombok.Data;

@Data
public class ResponseDTO<T> {
    // Getters and Setters
    private int statusCode;
    private T data;

    public ResponseDTO(int statusCode, T data) {
        this.statusCode = statusCode;
        this.data = data;
    }
}
