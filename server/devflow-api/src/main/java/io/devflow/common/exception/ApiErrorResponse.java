package io.devflow.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private String message;
    private int status;
    private Instant timestamp;
    private Map<String, String> errors; // Field specific errors, if any

    public ApiErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = Instant.now();
    }
}
