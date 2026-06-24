package io.devflow.issues.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateLabelRequest {

    @NotBlank(message = "Label name is required")
    @Size(max = 80, message = "Label name must not exceed 80 characters")
    private String name;

    @NotBlank(message = "Label color is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "Invalid color hex code")
    private String color;

    private String description;
}
