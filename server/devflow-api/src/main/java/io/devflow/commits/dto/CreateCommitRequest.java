package io.devflow.commits.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommitRequest {
    
    @NotBlank(message = "Commit message is required")
    private String message;

    private String branchName; // defaults to repo default branch if null
    
    private String filePath; // simple MVP support: only 1 file change at a time
    
    private String fileContent; // if updating/creating file
    
    private boolean isDelete; // true if deleting a file
}
