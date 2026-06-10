package io.devflow.issues.dto;

import io.devflow.issues.enums.IssueStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateIssueRequest {
    private String title;
    private String body;
    private IssueStatus status;
}
