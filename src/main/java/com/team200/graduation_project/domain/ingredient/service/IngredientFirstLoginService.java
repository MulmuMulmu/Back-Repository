package com.team200.graduation_project.domain.ingredient.service;

import com.team200.graduation_project.domain.ingredient.dto.request.ExtraInfoRequest;
import com.team200.graduation_project.domain.ingredient.entity.Ingredient;
import com.team200.graduation_project.domain.ingredient.repository.IngredientRepository;
import com.team200.graduation_project.domain.user.entity.User;
import com.team200.graduation_project.domain.user.entity.UserPreference;
import com.team200.graduation_project.domain.user.repository.UserPreferenceRepository;
import com.team200.graduation_project.domain.user.repository.UserRepository;
import com.team200.graduation_project.global.apiPayload.code.GeneralErrorCode;
import com.team200.graduation_project.global.apiPayload.exception.GeneralException;
import com.team200.graduation_project.global.jwt.JwtTokenProvider;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class IngredientFirstLoginService {

    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String saveExtraInfo(String authorizationHeader, ExtraInfoRequest request) {
        if (request == null) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST);
        }

        User user = findUserFromAuthorizationHeader(authorizationHeader);

        userPreferenceRepository.deleteByUser(user);

        List<UserPreference> preferences = new ArrayList<>();
        preferences.addAll(toUserPreferences(user, request.getAllergies(), "ALLERGY"));
        preferences.addAll(toUserPreferences(user, request.getPreferIngredients(), "PREFER"));
        preferences.addAll(toUserPreferences(user, request.getDispreferIngredients(), "DISPREFER"));

        userPreferenceRepository.saveAll(preferences);
        user.updateFirstLogin(false);

        return "성공적으로 저장되었습니다.";
    }

    private User findUserFromAuthorizationHeader(String authorizationHeader) {
        String token = extractAccessToken(authorizationHeader);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }

        String userId = jwtTokenProvider.getSubject(token);
        return userRepository.findByIdIs(userId)
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

    private List<UserPreference> toUserPreferences(User user, List<String> ingredientNames, String type) {
        if (ingredientNames == null || ingredientNames.isEmpty()) {
            return List.of();
        }

        return ingredientNames.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .map(name -> createUserPreference(user, name, type))
                .toList();
    }

    private UserPreference createUserPreference(User user, String ingredientName, String type) {
        Ingredient ingredient = ingredientRepository.findByIngredientName(ingredientName)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.BAD_REQUEST));

        return UserPreference.builder()
                .user(user)
                .ingredient(ingredient)
                .type(type)
                .build();
    }
}

