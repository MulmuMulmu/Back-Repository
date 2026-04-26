package com.team200.graduation_project.domain.admin.service;

import com.team200.graduation_project.domain.admin.dto.request.AdminIngredientRequest;
import com.team200.graduation_project.domain.admin.dto.request.AdminLoginRequest;
import com.team200.graduation_project.domain.admin.dto.request.AdminUserActionRequest;
import com.team200.graduation_project.domain.admin.dto.response.AdminLoginResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminReportDetailResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminReportListResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminShareDetailResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminOcrListResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminTodayReportResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminUserListResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminUserShareListResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminTodayShareResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminUserDashboardResponse;
import com.team200.graduation_project.domain.admin.exception.AdminErrorCode;
import com.team200.graduation_project.domain.admin.exception.AdminException;
import com.team200.graduation_project.domain.ingredient.entity.Ingredient;
import com.team200.graduation_project.domain.ingredient.repository.IngredientRepository;
import com.team200.graduation_project.domain.ingredient.repository.UserIngredientRepository;
import com.team200.graduation_project.domain.ocr.entity.Ocr;
import com.team200.graduation_project.domain.ocr.repository.OcrRepository;
import com.team200.graduation_project.domain.share.entity.Report;
import com.team200.graduation_project.domain.share.entity.ReportStatus;
import com.team200.graduation_project.domain.share.entity.Share;
import com.team200.graduation_project.domain.share.repository.ReportRepository;
import com.team200.graduation_project.domain.share.repository.ShareRepository;
import com.team200.graduation_project.domain.user.entity.Role;
import com.team200.graduation_project.domain.user.entity.User;
import com.team200.graduation_project.domain.user.entity.UserStatus;
import com.team200.graduation_project.domain.user.repository.UserRepository;
import com.team200.graduation_project.global.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final IngredientRepository ingredientRepository;
    private final UserIngredientRepository userIngredientRepository;
    private final OcrRepository ocrRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final ReportRepository reportRepository;
    private final ShareRepository shareRepository;

    public void addIngredients(List<AdminIngredientRequest> requests) {
        List<Ingredient> ingredients = requests.stream()
                .map(request -> Ingredient.builder()
                        .ingredientName(request.getIngredient())
                        .category(request.getCategory())
                        .build())
                .collect(Collectors.toList());

        ingredientRepository.saveAll(ingredients);
    }

    public AdminLoginResponse login(AdminLoginRequest request) {
        User user = userRepository.findByUserIdIsAndDeletedAtIsNull(request.getEmail())
                .orElseThrow(() -> new AdminException(AdminErrorCode.ADMIN_LOGIN_FAILED));

        if (user.getRole() != Role.ADMIN) {
            throw new AdminException(AdminErrorCode.ADMIN_LOGIN_FAILED);
        }

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AdminException(AdminErrorCode.ADMIN_LOGIN_FAILED);
        }

        String jwt = jwtTokenService.issueTokenPair(user.getUserId()).accessToken();
        return AdminLoginResponse.builder()
                .jwt(jwt)
                .build();
    }

    public String logout() {
        try {
            return "로그아웃 되었습니다.";
        } catch (Exception e) {
            throw new AdminException(AdminErrorCode.ADMIN_LOGOUT_ERROR);
        }
    }

    public AdminUserDashboardResponse getUserDashboard() {
        try{
            Long totalUsers = userRepository.countByRole(Role.USER);
            Long atLeastOneWarming = userRepository.countByStatus(UserStatus.WARMING);
            Long permanentSuspension = userRepository.countByStatus(UserStatus.BLOCKED);

            return AdminUserDashboardResponse.builder()
                    .totalUsers(totalUsers)
                    .atLeastOneWarming(atLeastOneWarming)
                    .permanentSuspension(permanentSuspension)
                    .build();
        } catch (Exception e) {
            throw new AdminException(AdminErrorCode.ADMIN_DASHBOARD_ERROR);
        }
    }

    public AdminTodayReportResponse getTodayReports() {
        try {
            LocalDateTime start = LocalDateTime.now().with(LocalTime.MIN);
            LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);

            Long todayReports = reportRepository.countByCreateTimeBetween(start, end);

            return AdminTodayReportResponse.builder()
                    .todayReports(todayReports)
                    .build();
        } catch (Exception e) {
            throw new AdminException(AdminErrorCode.ADMIN_TODAY_REPORT_ERROR);
        }
    }

    public AdminTodayShareResponse getTodayShares() {
        try {
            LocalDateTime start = LocalDateTime.now().with(LocalTime.MIN);
            LocalDateTime end = LocalDateTime.now().with(LocalTime.MAX);

            Long todayShares = shareRepository.countByCreateTimeBetween(start, end);

            return AdminTodayShareResponse.builder()
                    .todayShares(todayShares)
                    .build();
        } catch (Exception e) {
            throw new AdminException(AdminErrorCode.ADMIN_TODAY_SHARE_ERROR);
        }
    }

    public AdminReportListResponse getReportList(LocalDate date, String type) {
        try {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            List<Report> reports;
            if ("completed".equalsIgnoreCase(type)) {
                reports = reportRepository.findAllByCreateTimeBetweenAndStatus(start, end, ReportStatus.COMPLETED);
            } else if ("notCompleted".equalsIgnoreCase(type)) {
                reports = reportRepository.findAllByCreateTimeBetweenAndStatus(start, end, ReportStatus.NOT_COMPLETED);
            } else {
                reports = reportRepository.findAllByCreateTimeBetween(start, end);
            }

            List<AdminReportListResponse.ReportItemDTO> reportItems = reports.stream()
                    .map(report -> AdminReportListResponse.ReportItemDTO.builder()
                            .reportId(report.getReportId())
                            .shareId(report.getShare().getShareId())
                            .reporterName(report.getReporter().getNickName())
                            .content(report.getContent())
                            .status(report.getStatus().getDescription())
                            .build())
                    .collect(Collectors.toList());

            return AdminReportListResponse.builder()
                    .reports(reportItems)
                    .build();
        } catch (Exception e) {
            throw new AdminException(AdminErrorCode.ADMIN_REPORT_LIST_ERROR);
        }
    }

    public AdminReportDetailResponse getReportDetail(UUID reportId) {
        try {
            Report report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new AdminException(AdminErrorCode.ADMIN_REPORT_DETAIL_ERROR));

            User reporter = report.getReporter();
            User reported = report.getShare().getUser();

            return AdminReportDetailResponse.builder()
                    .reporterName(reporter.getNickName())
                    .reportedName(reported.getNickName())
                    .totalWarming(reported.getWarmingCount() != null ? reported.getWarmingCount() : 0L)
                    .title(report.getTitle())
                    .content(report.getContent())
                    .build();
        } catch (AdminException e) {
            throw e;
        } catch (Exception e) {
            throw new AdminException(AdminErrorCode.ADMIN_REPORT_DETAIL_ERROR);
        }
    }

    public void maskSharePost(UUID shareId) {
        try {
            Share share = shareRepository.findById(shareId)
                    .orElseThrow(() -> new AdminException(AdminErrorCode.ADMIN_SHARE_MASKING_ERROR));

            share.mask();
            shareRepository.save(share);
        } catch (AdminException e) {
            throw e;
        } catch (Exception e) {
            throw new AdminException(AdminErrorCode.ADMIN_SHARE_MASKING_ERROR);
        }
    }

    public String takeActionAgainstUser(AdminUserActionRequest request) {
        try {
            User user = userRepository.findByUserIdIsAndDeletedAtIsNull(request.getUserId())
                    .orElseThrow(() -> new AdminException(AdminErrorCode.ADMIN_USER_ACTION_ERROR));

            String resultMessage;
            if ("영구정지".equals(request.getStatus())) {
                user.block();
                resultMessage = "사용자가 영구 정지 되었습니다.";
            } else if ("사용자 경고".equals(request.getStatus())) {
                user.addWarning();
                resultMessage = "사용자에게 경고 하나를 부여했습니다.";
            } else {
                throw new AdminException(AdminErrorCode.ADMIN_USER_ACTION_ERROR);
            }

            userRepository.save(user);
            return resultMessage;
        } catch (AdminException e) {
            throw e;
        } catch (Exception e) {
            throw new AdminException(AdminErrorCode.ADMIN_USER_ACTION_ERROR);
        }
    }

    public AdminShareDetailResponse getShareDetail(UUID shareId) {
        try {
            Share share = shareRepository.findById(shareId)
                    .orElseThrow(() -> new AdminException(AdminErrorCode.ADMIN_SHARE_DETAIL_ERROR));

            String imageUrl = share.getSharePicture() != null ? share.getSharePicture().getPictureUrl() : null;
            String ingredientName = share.getUserIngredient() != null && share.getUserIngredient().getIngredient() != null 
                    ? share.getUserIngredient().getIngredient().getIngredientName() 
                    : null;

            return AdminShareDetailResponse.builder()
                    .image(imageUrl)
                    .sellerName(share.getUser().getNickName())
                    .title(share.getTitle())
                    .category(share.getCategory())
                    .description(share.getContent())
                    .ingredient(ingredientName)
                    .createTime(share.getCreateTime())
                    .build();
        } catch (AdminException e) {
            throw e;
        } catch (Exception e) {
            throw new AdminException(AdminErrorCode.ADMIN_SHARE_DETAIL_ERROR);
        }
    }

    public List<AdminUserListResponse> getUserList(String userId) {
        try {
            List<User> users;
            if ("all".equals(userId)) {
                users = userRepository.findAllByDeletedAtIsNull();
            } else {
                users = userRepository.findByUserIdIsAndDeletedAtIsNull(userId)
                        .map(Collections::singletonList)
                        .orElse(Collections.emptyList());
            }

            List<AdminUserListResponse> result = new ArrayList<>();
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                Long totalShare = shareRepository.countByUserAndDeletedAtIsNull(user);

                result.add(AdminUserListResponse.builder()
                        .number(i + 1)
                        .userId(user.getUserId())
                        .nickName(user.getNickName())
                        .totalWarming(user.getWarmingCount() != null ? user.getWarmingCount() : 0L)
                        .totalShare(totalShare)
                        .build());
            }
            return result;
        } catch (Exception e) {
            throw new AdminException(AdminErrorCode.ADMIN_USER_LIST_ERROR);
        }
    }

    public List<AdminUserShareListResponse> getUserShareList(String userId) {
        try {
            User user = userRepository.findByUserIdIsAndDeletedAtIsNull(userId)
                    .orElseThrow(() -> new AdminException(AdminErrorCode.ADMIN_USER_SHARE_LIST_ERROR));

            List<Share> shares = shareRepository.findAllByUserAndDeletedAtIsNullOrderByCreateTimeDesc(user);

            return shares.stream()
                    .map(share -> AdminUserShareListResponse.builder()
                            .shareId(share.getShareId())
                            .image(share.getSharePicture() != null ? share.getSharePicture().getPictureUrl() : null)
                            .title(share.getTitle())
                            .content(share.getContent())
                            .build())
                    .collect(Collectors.toList());
        } catch (AdminException e) {
            throw e;
        } catch (Exception e) {
            throw new AdminException(AdminErrorCode.ADMIN_USER_SHARE_LIST_ERROR);
        }
    }

    public List<AdminOcrListResponse> getOcrList() {
        try {
            return ocrRepository.findAll().stream()
                    .map(ocr -> AdminOcrListResponse.builder()
                            .ocrId(ocr.getOcrId())
                            .nickName(ocr.getUser() != null ? ocr.getUser().getNickName() : null)
                            .createTime(ocr.getCreateTime())
                            .accuracy(ocr.getAccuracy())
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new AdminException(AdminErrorCode.ADMIN_OCR_LIST_ERROR);
        }
    }
}
