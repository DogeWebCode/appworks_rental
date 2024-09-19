package tw.school.rental_backend.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ResponseDTO<T> {

    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String nextPage;

    public ResponseDTO(T data) {
        this.data = data;
    }
}
