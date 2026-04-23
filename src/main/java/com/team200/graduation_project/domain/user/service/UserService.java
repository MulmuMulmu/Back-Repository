package com.team200.graduation_project.domain.user.service;

import com.team200.graduation_project.domain.user.dto.request.ChangePasswordRequest;
import com.team200.graduation_project.domain.user.dto.request.KakaoSignupRequest;
import com.team200.graduation_project.domain.user.dto.request.LoginRequest;
import com.team200.graduation_project.domain.user.dto.request.UserSignupRequest;
import com.team200.graduation_project.domain.user.dto.response.LoginResponse;
import com.team200.graduation_project.domain.user.entity.User;
import com.team200.graduation_project.domain.user.entity.Role;
import com.team200.graduation_project.domain.user.entity.UserStatus;
import com.team200.graduation_project.domain.user.exception.UserErrorCode;
import com.team200.graduation_project.domain.user.exception.UserException;
import com.team200.graduation_project.domain.user.repository.UserRepository;
import com.team200.graduation_project.global.apiPayload.code.GeneralErrorCode;
import com.team200.graduation_project.global.apiPayload.exception.GeneralException;
import com.team200.graduation_project.global.jwt.JwtTokenPair;
import com.team200.graduation_project.global.jwt.JwtTokenProvider;
import com.team200.graduation_project.global.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenService jwtTokenService;

    public String checkIdDuplicated(String id) {
        if (!StringUtils.hasText(id)) {
            throw new UserException(UserErrorCode.USER_BAD_REQUEST);
        }

        if (userRepository.existsByUserIdIs(id)) {
            throw new UserException(UserErrorCode.USER_ID_DUPLICATED);
        }

        return "사용가능한 id 입니다.";
    }

    @Transactional
    public String signup(UserSignupRequest request) {
        validateSignupRequest(request);

        if (userRepository.existsByUserIdIs(request.getId())) {
            throw new UserException(UserErrorCode.USER_ID_DUPLICATED);
        }

        try {
            User user = User.builder()
                    .userId(request.getId())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .nickName(request.getName())
                    .firstLogin(true)
                    .warmingCount(0L)
                    .deletedAt(null)
                    .status(UserStatus.NORMAL)
                    .role(Role.USER)
                    .build();

            userRepository.save(user);
            return "회원가입이 완료되었습니다";
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new UserException(UserErrorCode.USER_SIGNUP_FAILED);
        }
    }

    @Transactional
    public String signupWithKakao(KakaoSignupRequest request) {
        validateKakaoAuthorizationCodeForSignup(request);
        String kakaoId = kakaoUserIdFromAuthorizationCode(request.getAuthorizationCode());

        try {
            if (!userRepository.existsByUserIdIs(kakaoId)) {
                User user = User.builder()
                        .userId(kakaoId)
                        .password(null)
                        .nickName("kakao_user")
                        .firstLogin(true)
                        .warmingCount(0L)
                        .deletedAt(null)
                        .status(UserStatus.NORMAL)
                        .role(Role.USER)
                        .build();
                userRepository.save(user);
            }
            return "회원가입이 완료되었습니다";
        } catch (Exception e) {
            throw new UserException(UserErrorCode.USER_SIGNUP_FAILED);
        }
    }

    private void validateSignupRequest(UserSignupRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getName())
                || !StringUtils.hasText(request.getId())
                || !StringUtils.hasText(request.getPassword())
                || !StringUtils.hasText(request.getCheckPassword())) {
            throw new UserException(UserErrorCode.USER_BAD_REQUEST);
        }

        if (!request.getPassword().equals(request.getCheckPassword())) {
            throw new UserException(UserErrorCode.USER_BAD_REQUEST);
        }
    }

    private void validateKakaoAuthorizationCodeForSignup(KakaoSignupRequest request) {
        if (request == null || !StringUtils.hasText(request.getAuthorizationCode())) {
            throw new UserException(UserErrorCode.USER_KAKAO_TOKEN_NOT_FOUND);
        }
    }

    private String kakaoUserIdFromAuthorizationCode(String authorizationCode) {
        String accessToken = "kakao_access_token_" + authorizationCode;
        return "kakao_" + Math.abs(accessToken.hashCode());
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        if (request == null || !StringUtils.hasText(request.getId()) || !StringUtils.hasText(request.getPassword())) {
            throw new UserException(UserErrorCode.USER_LOGIN_FAILED);
        }

        User user = userRepository
                .findByUserIdIsAndDeletedAtIsNull(request.getId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_LOGIN_FAILED));

        if (user.getStatus() != UserStatus.NORMAL) {
            throw new UserException(UserErrorCode.USER_LOGIN_FAILED);
        }

        if (!StringUtils.hasText(user.getPassword())
                || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserException(UserErrorCode.USER_LOGIN_FAILED);
        }

        JwtTokenPair tokenPair = jwtTokenService.issueTokenPair(user.getUserId());
        return new LoginResponse(tokenPair.accessToken(), user.getFirstLogin());
    }

    @Transactional(readOnly = true)
    public LoginResponse loginWithKakao(KakaoSignupRequest request) {
        if (request == null || !StringUtils.hasText(request.getAuthorizationCode())) {
            throw new UserException(UserErrorCode.USER_LOGIN_FAILED);
        }

        String kakaoId = kakaoUserIdFromAuthorizationCode(request.getAuthorizationCode());
        User user = userRepository
                .findByUserIdIsAndDeletedAtIsNull(kakaoId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_LOGIN_FAILED));

        if (user.getStatus() != UserStatus.NORMAL) {
            throw new UserException(UserErrorCode.USER_LOGIN_FAILED);
        }

        JwtTokenPair tokenPair = jwtTokenService.issueTokenPair(user.getUserId());
        return new LoginResponse(tokenPair.accessToken(), user.getFirstLogin());
    }

    @Transactional
    public String changePassword(String authorizationHeader, ChangePasswordRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getOldPassword())
                || !StringUtils.hasText(request.getNewPassword())) {
            throw new UserException(UserErrorCode.USER_BAD_REQUEST);
        }

        try {
            User user = findUserFromAuthorizationHeader(authorizationHeader);

            if (!StringUtils.hasText(user.getPassword())
                    || !passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new UserException(UserErrorCode.USER_PASSWORD_MISMATCH);
            }

            user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
            return "비밀번호가 성공적으로 변경되었습니다.";
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            throw new UserException(UserErrorCode.USER_PASSWORD_CHANGE_FAILED);
        }
    }

    public String logout(String authorizationHeader) {
        try {
            return "로그아웃 완료되었습니다.";
        } catch (Exception e) {
            throw new UserException(UserErrorCode.USER_LOGOUT_FAILED);
        }
    }

    @Transactional
    public String deleteAccount(String authorizationHeader) {
        try {
            User user = findUserFromAuthorizationHeader(authorizationHeader);
            user.softDelete();
            return "회원탈퇴가 완료되었습니다.";
        } catch (UserException | GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new UserException(UserErrorCode.USER_DELETION_FAILED);
        }
    }

    private User findUserFromAuthorizationHeader(String authorizationHeader) {
        String token = extractAccessToken(authorizationHeader);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }

        String userId = jwtTokenProvider.getSubject(token);
        return userRepository.findByUserIdIsAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.UNAUTHORIZED));
    }

    private String extractAccessToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }

        String bearerPrefix = "Bearer ";
        if (authorizationHeader.startsWith(bearerPrefix)) {
            return authorizationHeader.substring(bearerPrefix.length()).trim();
        }

        return authorizationHeader.trim();
    }
}
