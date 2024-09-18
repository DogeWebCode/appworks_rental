package tw.school.rental_backend.data.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResponseDTO<T> {

    private T data;
    private String nextPage;

    public ResponseDTO(T data) {
        this.data = data;
    }

}
