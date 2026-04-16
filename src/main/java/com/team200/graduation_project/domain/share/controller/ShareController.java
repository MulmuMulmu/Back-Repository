package com.team200.graduation_project.domain.share.controller;

import com.team200.graduation_project.domain.share.dto.request.LocationRequest;
import com.team200.graduation_project.domain.share.dto.response.LocationResponse;
import com.team200.graduation_project.domain.share.service.ShareService;
import com.team200.graduation_project.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
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
    @PostMapping("/adding/location")
    public ApiResponse<LocationResponse> addLocation(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody LocationRequest request) {
        return ApiResponse.onSuccess(shareService.addLocation(authorizationHeader, request));
    }
}
