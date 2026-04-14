package com.team200.graduation_project.domain.ingredient.controller;

import com.team200.graduation_project.domain.ingredient.dto.request.AllergyUpdateRequest;
import com.team200.graduation_project.domain.ingredient.dto.request.PreferUpdateRequest;
import com.team200.graduation_project.domain.ingredient.dto.request.UserIngredientInputRequest;
import com.team200.graduation_project.domain.ingredient.service.IngredientService;
import com.team200.graduation_project.domain.ingredient.dto.request.ExtraInfoRequest;
import com.team200.graduation_project.domain.ingredient.service.IngredientFirstLoginService;
import com.team200.graduation_project.domain.ingredient.service.UserIngredientService;
import com.team200.graduation_project.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingredient")
@RequiredArgsConstructor
public class IngredientController implements IngredientControllerDocs {

    private final IngredientService ingredientService;
    private final IngredientFirstLoginService ingredientFirstLoginService;
    private final UserIngredientService userIngredientService;

    @GetMapping("/search")
    @Override
    public ApiResponse<?> searchIngredients(@RequestParam String keyword) {
        return ApiResponse.onSuccess(ingredientService.searchIngredients(keyword));
    }

    @PostMapping("/input")
    @Override
    public ApiResponse<String> inputIngredients(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody java.util.List<UserIngredientInputRequest> request) {
        return ApiResponse.onSuccess(userIngredientService.saveUserIngredients(authorizationHeader, request));
    }

    @GetMapping("/all/my")
    @Override
    public ApiResponse<java.util.List<com.team200.graduation_project.domain.ingredient.dto.response.UserIngredientSearchResponse>> searchMyIngredients(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @ModelAttribute com.team200.graduation_project.domain.ingredient.dto.request.UserIngredientSearchRequest request) {
        return ApiResponse.onSuccess(userIngredientService.searchUserIngredients(authorizationHeader, request));
    }

    @GetMapping("/experationDate/3")
    @Override
    public ApiResponse<Integer> countExpiringIngredients(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        return ApiResponse.onSuccess(userIngredientService.countExpiringIngredients(authorizationHeader, 3));
    }

    @PostMapping("/first/login")
    @Override
    public ApiResponse<String> saveExtraInfo(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody ExtraInfoRequest request
    ) {
        return ApiResponse.onSuccess(ingredientFirstLoginService.saveExtraInfo(authorizationHeader, request));
    }

    @PutMapping("/allergy")
    @Override
    public ApiResponse<String> updateAllergy(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody AllergyUpdateRequest request
    ) {
        return ApiResponse.onSuccess(ingredientFirstLoginService.updateAllergy(authorizationHeader, request));
    }

    @PutMapping("/prefer")
    @Override
    public ApiResponse<String> updatePrefer(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody PreferUpdateRequest request
    ) {
        return ApiResponse.onSuccess(ingredientFirstLoginService.updatePrefer(authorizationHeader, request));
    }
}
