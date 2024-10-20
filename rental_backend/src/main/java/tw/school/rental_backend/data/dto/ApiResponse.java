package tw.school.rental_backend.data.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;

    public ApiResponse() {
    }

    public ApiResponse(T data) {
        this.success = true;
        this.data = data;
    }

    public ApiResponse(String message) {
        this.success = false;
        this.message = message;
    }
}
