package com.team200.graduation_project.domain.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserActionRequest {
    private String userId;
    private String status;
    private UUID reportId;
}
