package com.team200.graduation_project.domain.user.controller;

import com.team200.graduation_project.domain.user.dto.request.KakaoSignupRequest;
import com.team200.graduation_project.domain.user.dto.request.LoginRequest;
import com.team200.graduation_project.domain.user.dto.request.UserIdCheckRequest;
import com.team200.graduation_project.domain.user.dto.request.UserSignupRequest;
import com.team200.graduation_project.domain.user.dto.response.LoginResponse;
import com.team200.graduation_project.domain.user.service.UserService;
import com.team200.graduation_project.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup/idCheck")
    public ApiResponse<String> checkIdDuplicated(@RequestBody UserIdCheckRequest request) {
        return ApiResponse.onSuccess(userService.checkIdDuplicated(request.getId()));
    }

    @PostMapping("/signup")
    public ApiResponse<String> signup(@RequestBody UserSignupRequest request) {
        return ApiResponse.onSuccess(userService.signup(request));
    }

    @PostMapping("/signup/kakao")
    public ApiResponse<String> signupWithKakao(@RequestBody KakaoSignupRequest request) {
        return ApiResponse.onSuccess(userService.signupWithKakao(request));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.onSuccess(userService.login(request));
    }

    @PostMapping("/login/kakao")
    public ApiResponse<LoginResponse> loginWithKakao(@RequestBody KakaoSignupRequest request) {
        return ApiResponse.onSuccess(userService.loginWithKakao(request));
    }
}
