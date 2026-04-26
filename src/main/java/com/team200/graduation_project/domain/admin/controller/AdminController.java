package com.team200.graduation_project.domain.admin.controller;

import com.team200.graduation_project.domain.admin.dto.request.AdminIngredientRequest;
import com.team200.graduation_project.domain.admin.dto.request.AdminLoginRequest;
import com.team200.graduation_project.domain.admin.dto.request.AdminUserActionRequest;
import com.team200.graduation_project.domain.admin.dto.response.AdminLoginResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminReportDetailResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminReportListResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminShareDetailResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminOcrDetailResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminOcrIngredientResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminOcrListResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminUserListResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminUserShareListResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminTodayReportResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminTodayShareResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminUserDashboardResponse;
import com.team200.graduation_project.domain.admin.service.AdminService;
import com.team200.graduation_project.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    @PatchMapping("/report/post/masking")
    public ApiResponse<String> maskSharePost(
            @RequestHeader("Authorization") String token,
            @RequestParam("shareId") UUID shareId
    ) {
        adminService.maskSharePost(shareId);
        return ApiResponse.onSuccess("게시글이 숨김 처리 되었습니다.");
    }

    @Override
    @PatchMapping("/report/users")
    public ApiResponse<String> takeActionAgainstUser(
            @RequestHeader("Authorization") String token,
            @RequestBody AdminUserActionRequest request
    ) {
        return ApiResponse.onSuccess(adminService.takeActionAgainstUser(request));
    }

    @Override
    @GetMapping("/shares/one")
    public ApiResponse<AdminShareDetailResponse> getShareDetail(
            @RequestHeader("Authorization") String token,
            @RequestParam("shareId") UUID shareId
    ) {
        return ApiResponse.onSuccess(adminService.getShareDetail(shareId));
    }

    @Override
    @GetMapping("/users/list")
    public ApiResponse<List<AdminUserListResponse>> getUserList(
            @RequestHeader("Authorization") String token,
            @RequestParam("userId") String userId
    ) {
        return ApiResponse.onSuccess(adminService.getUserList(userId));
    }

    @Override
    @GetMapping("/users/shares/list")
    public ApiResponse<List<AdminUserShareListResponse>> getUserShareList(
            @RequestHeader("Authorization") String token,
            @RequestParam("userId") String userId
    ) {
        return ApiResponse.onSuccess(adminService.getUserShareList(userId));
    }

    @Override
    @GetMapping("/ocr/list")
    public ApiResponse<List<AdminOcrListResponse>> getOcrList(
            @RequestHeader("Authorization") String token
    ) {
        return ApiResponse.onSuccess(adminService.getOcrList());
    }

    @Override
    @GetMapping("/ocr/one")
    public ApiResponse<AdminOcrDetailResponse> getOcrDetail(
            @RequestHeader("Authorization") String token,
            @RequestParam("ocrId") UUID ocrId
    ) {
        return ApiResponse.onSuccess(adminService.getOcrDetail(ocrId));
    }

    @Override
    @GetMapping("/ocr/one/ingredients")
    public ApiResponse<List<AdminOcrIngredientResponse>> getOcrIngredients(
            @RequestHeader("Authorization") String token,
            @RequestParam("ocrId") UUID ocrId
    ) {
        return ApiResponse.onSuccess(adminService.getOcrIngredients(ocrId));
    }
}
