package com.team200.graduation_project.domain.share.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShareRequestDTO {
    private List<MultipartFile> image;
    private String title;
    private String description;
    private String category;
    private LocalDate expirationDate;
}
