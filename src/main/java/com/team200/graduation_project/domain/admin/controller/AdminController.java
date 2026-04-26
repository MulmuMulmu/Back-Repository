package com.team200.graduation_project.domain.admin.controller;

import com.team200.graduation_project.domain.admin.dto.request.AdminIngredientRequest;
import com.team200.graduation_project.domain.admin.dto.request.AdminLoginRequest;
import com.team200.graduation_project.domain.admin.dto.response.AdminLoginResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminReportDetailResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminReportListResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminTodayReportResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminTodayShareResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminUserDashboardResponse;
import com.team200.graduation_project.domain.admin.service.AdminService;
import com.team200.graduation_project.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController implements AdminControllerDocs {

    private final AdminService adminService;

    @Override
    @PostMapping("/ingredient/input")
    public ApiResponse<String> addIngredients(@RequestBody List<AdminIngredientRequest> requests) {
        try {
            adminService.addIngredients(requests);
            return ApiResponse.onSuccess("식재료가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            return ApiResponse.onFailure("COMMON500", "식재료를 등록할 수 없습니다.");
        }
    }

    @Override
    @PostMapping("/auth/login")
    public ApiResponse<AdminLoginResponse> login(@RequestBody AdminLoginRequest request) {
        return ApiResponse.onSuccess(adminService.login(request));
    }

    @Override
    @PostMapping("/auth/logout")
    public ApiResponse<String> logout(@RequestHeader("Authorization") String token) {
        return ApiResponse.onSuccess(adminService.logout());
    }

    @Override
    @GetMapping("/dashboard/user")
    public ApiResponse<AdminUserDashboardResponse> getUserDashboard(@RequestHeader("Authorization") String token) {
        return ApiResponse.onSuccess(adminService.getUserDashboard());
    }

    @Override
    @GetMapping("/dashboard/today/report")
    public ApiResponse<AdminTodayReportResponse> getTodayReports(@RequestHeader("Authorization") String token) {
        return ApiResponse.onSuccess(adminService.getTodayReports());
    }

    @Override
    @GetMapping("/dashboard/today/share")
    public ApiResponse<AdminTodayShareResponse> getTodayShares(@RequestHeader("Authorization") String token) {
        return ApiResponse.onSuccess(adminService.getTodayShares());
    }

    @Override
    @GetMapping("/report/list")
    public ApiResponse<AdminReportListResponse> getReportList(
            @RequestHeader("Authorization") String token,
            @RequestParam("Date") LocalDate date,
            @RequestParam("type") String type
    ) {
        return ApiResponse.onSuccess(adminService.getReportList(date, type));
    }

    @Override
    @GetMapping("/report/one")
    public ApiResponse<AdminReportDetailResponse> getReportDetail(
            @RequestHeader("Authorization") String token,
            @RequestParam("reportId") UUID reportId
    ) {
        return ApiResponse.onSuccess(adminService.getReportDetail(reportId));
    }

    @Override
    @PostMapping("/report/post/masking")
    public ApiResponse<String> maskSharePost(
            @RequestHeader("Authorization") String token,
            @RequestParam("shareId") UUID shareId
    ) {
        adminService.maskSharePost(shareId);
        return ApiResponse.onSuccess("게시글이 숨김 처리 되었습니다.");
    }
}
