package com.team200.graduation_project.domain.ingredient.service;

import com.team200.graduation_project.domain.ingredient.dto.request.UserIngredientInputRequest;
import com.team200.graduation_project.domain.ingredient.entity.Ingredient;
import com.team200.graduation_project.domain.ingredient.entity.UserIngredient;
import com.team200.graduation_project.domain.ingredient.repository.IngredientRepository;
import com.team200.graduation_project.domain.ingredient.repository.UserIngredientRepository;
import com.team200.graduation_project.domain.user.entity.User;
import com.team200.graduation_project.domain.user.repository.UserRepository;
import com.team200.graduation_project.global.apiPayload.code.GeneralErrorCode;
import com.team200.graduation_project.global.apiPayload.exception.GeneralException;
import com.team200.graduation_project.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import com.team200.graduation_project.domain.ingredient.dto.response.UserIngredientExpirationResponse;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class UserIngredientService {

    private final UserIngredientRepository userIngredientRepository;
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String saveUserIngredients(String authorizationHeader, List<UserIngredientInputRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST);
        }

        try {
            User user = findUserFromAuthorizationHeader(authorizationHeader);

            List<UserIngredient> userIngredients = requests.stream().map(request -> {
                Ingredient ingredient = ingredientRepository.findByIngredientName(request.getIngredient())
                        .orElseThrow(() -> new GeneralException(GeneralErrorCode.BAD_REQUEST));

                return UserIngredient.builder()
                        .user(user)
                        .ingredient(ingredient)
                        .expirationDate(request.getExpirationDate())
                        .status("INPUT") // Or leave null, using a default value based on current context
                        .build();
            }).collect(Collectors.toList());

            userIngredientRepository.saveAll(userIngredients);
            return "성공적으로 저장되었습니다.";
        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralException(GeneralErrorCode.INGREDIENT_SAVE_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public List<com.team200.graduation_project.domain.ingredient.dto.response.UserIngredientSearchResponse> searchUserIngredients(
            String authorizationHeader,
            com.team200.graduation_project.domain.ingredient.dto.request.UserIngredientSearchRequest request) {
        
        if (request == null || !StringUtils.hasText(request.getSort())) {
            throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_ARGUMENT);
        }

        try {
            User user = findUserFromAuthorizationHeader(authorizationHeader);
            org.springframework.data.domain.Sort sort = createSort(request.getSort());
            
            List<UserIngredient> items;
            if (request.getCategory() != null && !request.getCategory().isEmpty()) {
                items = userIngredientRepository.findByUserAndIngredient_CategoryIn(user, request.getCategory(), sort);
            } else {
                items = userIngredientRepository.findByUser(user, sort);
            }

            java.time.LocalDate today = java.time.LocalDate.now();
            List<com.team200.graduation_project.domain.ingredient.dto.response.UserIngredientSearchResponse> responses = new java.util.ArrayList<>();
            
            for (int i = 0; i < items.size(); i++) {
                UserIngredient item = items.get(i);
                long dDay = 0;
                if (item.getExpirationDate() != null) {
                    dDay = java.time.temporal.ChronoUnit.DAYS.between(today, item.getExpirationDate());
                }
                
                responses.add(com.team200.graduation_project.domain.ingredient.dto.response.UserIngredientSearchResponse.builder()
                        .sortRank(i + 1)
                        .ingredient(item.getIngredient().getIngredientName())
                        .dDay(dDay)
                        .expirationDate(item.getExpirationDate())
                        .build());
            }
            return responses;
        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralException(GeneralErrorCode.INGREDIENT_CALCULATION_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public Integer countExpiringIngredients(String authorizationHeader, int withinDays) {
        try {
            User user = findUserFromAuthorizationHeader(authorizationHeader);
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate endDate = today.plusDays(withinDays);

            // "소비기한 N일 내" = 오늘 포함하여 +N일 이하인 항목 계산 (이미 지난 항목도 세려면 LessThanEqual, 여기서는 오늘부터 +N일 이내로만 계산)
            return userIngredientRepository.countByUserAndExpirationDateBetween(user, today, endDate);
        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralException(GeneralErrorCode.INGREDIENT_COUNT_FAILED);
        }
    }

    @Transactional(readOnly = true)
    public List<UserIngredientExpirationResponse> getNearExpiringIngredients(String authorizationHeader) {
        try {
            User user = findUserFromAuthorizationHeader(authorizationHeader);
            List<UserIngredient> items = userIngredientRepository.findByUser(user, Sort.by(Sort.Direction.ASC, "expirationDate"));

            LocalDate today = LocalDate.now();

            Map<Long, List<String>> grouped = items.stream()
                    .filter(item -> item.getExpirationDate() != null)
                    .collect(Collectors.groupingBy(
                            item -> ChronoUnit.DAYS.between(today, item.getExpirationDate()),
                            Collectors.mapping(
                                    item -> item.getIngredient().getIngredientName(),
                                    Collectors.toList()
                            )
                    ));

            return grouped.entrySet().stream()
                    .map(entry -> UserIngredientExpirationResponse.builder()
                            .dDay(entry.getKey())
                            .ingredient(entry.getValue())
                            .build())
                    .sorted(Comparator.comparingLong(UserIngredientExpirationResponse::getDDay))
                    .collect(Collectors.toList());

        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralException(GeneralErrorCode.INGREDIENT_NEAR_EXPIRATION_FAILED);
        }
    }

    private org.springframework.data.domain.Sort createSort(String sortStr) {
        String[] parts = sortStr.split("&");
        if (parts.length != 2) {
            throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_ARGUMENT);
        }
        String field = parts[0];
        String order = parts[1];
        
        org.springframework.data.domain.Sort.Direction direction;
        if ("ascending".equalsIgnoreCase(order)) {
            direction = org.springframework.data.domain.Sort.Direction.ASC;
        } else if ("descending".equalsIgnoreCase(order)) {
            direction = org.springframework.data.domain.Sort.Direction.DESC;
        } else {
            throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_ARGUMENT);
        }
        
        String sortProperty;
        if ("date".equalsIgnoreCase(field)) {
            sortProperty = "expirationDate";
        } else if ("name".equalsIgnoreCase(field)) {
            sortProperty = "ingredient.ingredientName";
        } else {
            throw new GeneralException(GeneralErrorCode.INVALID_REQUEST_ARGUMENT);
        }
        
        return org.springframework.data.domain.Sort.by(direction, sortProperty);
    }

    private User findUserFromAuthorizationHeader(String authorizationHeader) {
        String token = extractAccessToken(authorizationHeader);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }

        String userId = jwtTokenProvider.getSubject(token);
        return userRepository.findByUserIdIs(userId)
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
