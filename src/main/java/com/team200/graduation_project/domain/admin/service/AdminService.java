package com.team200.graduation_project.domain.admin.service;

import com.team200.graduation_project.domain.admin.dto.request.AdminIngredientRequest;
import com.team200.graduation_project.domain.admin.dto.request.AdminLoginRequest;
import com.team200.graduation_project.domain.admin.dto.response.AdminLoginResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminTodayReportResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminTodayShareResponse;
import com.team200.graduation_project.domain.admin.dto.response.AdminUserDashboardResponse;
import com.team200.graduation_project.domain.admin.exception.AdminErrorCode;
import com.team200.graduation_project.domain.admin.exception.AdminException;
import com.team200.graduation_project.domain.ingredient.entity.Ingredient;
import com.team200.graduation_project.domain.ingredient.repository.IngredientRepository;
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final IngredientRepository ingredientRepository;
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
}
