package com.finskayaylochka.model.supporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * @author Alexandr Stegnin
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {

    private String message;

    private int status;

    private String error;

    public ApiResponse(String message) {
        this.message = message;
        this.status = HttpStatus.OK.value();
    }

    public ApiResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public static ApiResponse build422Response(String message) {
        return new ApiResponse(message, HttpStatus.PRECONDITION_FAILED.value());
    }

    public static ApiResponse build200Response(String message) {
        return new ApiResponse(message, HttpStatus.OK.value());
    }

}
