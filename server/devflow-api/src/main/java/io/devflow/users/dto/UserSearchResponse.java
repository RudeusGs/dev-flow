package io.devflow.users.dto;

import org.springframework.data.domain.Page;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchResponse {
    private Page<UserSummaryDto> users;
}
