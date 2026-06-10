package io.devflow.sourcefiles.dto;

import io.devflow.sourcefiles.enums.SourceFileType;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SourceFileDto {
    private String id;
    private String name;
    private String path;
    private SourceFileType fileType;
    private long sizeBytes;
    private boolean binary;
}
