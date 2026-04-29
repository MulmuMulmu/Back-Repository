package com.team200.graduation_project.domain.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReportDetailResponse {
    private String reporterName;
    private String reportedName;
    private String reportedNameId;
    private Long totalWarming;
    private String title;
    private String content;
}
