package com.team200.graduation_project.domain.admin.controller;

import com.team200.graduation_project.domain.admin.dto.request.AdminIngredientRequest;
import com.team200.graduation_project.domain.admin.dto.request.AdminLoginRequest;
import com.team200.graduation_project.domain.admin.dto.request.AdminUserActionRequest;
import com.team200.graduation_project.domain.admin.dto.response.AdminLoginResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminReportDetailResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminReportListResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminShareDetailResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminTodayReportResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminTodayShareResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminUserDashboardResponse;
import com.team200.graduation_project.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Admin", description = "관리자 관련 API")
public interface AdminControllerDocs {

    @Operation(summary = "식재료 수동 추가", description = "관리자가 식재료를 수동으로 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ApiResponse<String> addIngredients(@RequestBody List<AdminIngredientRequest> requests);

    @Operation(summary = "관리자 로그인", description = "관리자 계정으로 로그인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ApiResponse<AdminLoginResponse> login(@RequestBody AdminLoginRequest request);

    @Operation(summary = "관리자 로그아웃", description = "관리자 세션을 종료합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ApiResponse<String> logout(@RequestHeader("Authorization") String token);

    @Operation(summary = "사용자 통계 정보 조회", description = "관리자 대시보드에서 사용자 통계 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ApiResponse<AdminUserDashboardResponse> getUserDashboard(@RequestHeader("Authorization") String token);

    @Operation(summary = "당일 신고 건수 조회", description = "관리자 대시보드에서 당일 신고된 전체 건수를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ApiResponse<AdminTodayReportResponse> getTodayReports(@RequestHeader("Authorization") String token);

    @Operation(summary = "당일 새로운 나눔 수 조회", description = "관리자 대시보드에서 당일 생성된 새로운 나눔 게시글 수를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    ApiResponse<AdminTodayShareResponse> getTodayShares(@RequestHeader("Authorization") String token);

    @Operation(summary = "신고 목록 조회", description = "날짜와 처리 상태에 따라 신고 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "신고 목록을 조회할 수 없습니다.")
    })
    ApiResponse<AdminReportListResponse> getReportList(
            @RequestHeader("Authorization") String token,
            @RequestParam("Date") LocalDate date,
            @RequestParam("type") String type
    );

    @Operation(summary = "신고 내역 상세 조회", description = "신고 내역 한 건을 상세하게 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "신고 내역 한 건을 자세히 불러올 수 없습니다.")
    })
    ApiResponse<AdminReportDetailResponse> getReportDetail(
            @RequestHeader("Authorization") String token,
            @RequestParam("reportId") UUID reportId
    );

    @Operation(summary = "신고 게시글 숨김 처리", description = "신고된 게시글을 숨김 처리하여 다른 사용자가 볼 수 없게 합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글이 숨김 처리 되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "게시글을 숨김 처리 할 수 없습니다.")
    })
    ApiResponse<String> maskSharePost(
            @RequestHeader("Authorization") String token,
            @RequestParam("shareId") UUID shareId
    );

    @Operation(summary = "신고 사용자 조치", description = "신고된 사용자에게 경고를 부여하거나 영구 정지 처리를 합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "사용자 상태를 변경할 수 없습니다.")
    })
    ApiResponse<String> takeActionAgainstUser(
            @RequestHeader("Authorization") String token,
            @RequestBody AdminUserActionRequest request
    );

    @Operation(summary = "나눔글 상세 조회", description = "나눔글 한 건을 상세하게 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "나눔 정보를 불러올 수 없습니다.")
    })
    ApiResponse<AdminShareDetailResponse> getShareDetail(
            @RequestHeader("Authorization") String token,
            @RequestParam("shareId") UUID shareId
    );
}
