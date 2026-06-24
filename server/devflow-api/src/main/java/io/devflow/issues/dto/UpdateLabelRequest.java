package io.devflow.issues.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateLabelRequest {

    @Size(max = 80, message = "Label name must not exceed 80 characters")
    private String name;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Invalid color hex code")
    private String color;

    private String description;
}
