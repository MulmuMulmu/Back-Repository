package com.team200.graduation_project.domain.share.controller;

import com.team200.graduation_project.domain.share.dto.request.LocationRequest;
import com.team200.graduation_project.domain.share.dto.request.ShareRequestDTO;
import com.team200.graduation_project.domain.share.dto.response.LocationResponse;
import com.team200.graduation_project.domain.share.service.ShareService;
import com.team200.graduation_project.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/share")
@RequiredArgsConstructor
public class ShareController implements ShareControllerDocs {

    private final ShareService shareService;

    @Override
    @PostMapping("/location")
    public ApiResponse<LocationResponse> addLocation(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody LocationRequest request
    ) {
        return ApiResponse.onSuccess(shareService.addLocation(authorizationHeader, request));
    }

    @Override
    @PostMapping(value = "/posting", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> publishSharePosting(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @ModelAttribute ShareRequestDTO request
    ) {
        shareService.publishSharePosting(authorizationHeader, request);
        return ApiResponse.onSuccess("성공적으로 등록되었습니다.");
    }
}
