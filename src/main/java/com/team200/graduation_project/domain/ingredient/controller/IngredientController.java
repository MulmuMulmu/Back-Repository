package com.team200.graduation_project.domain.ingredient.controller;

import com.team200.graduation_project.domain.ingredient.service.IngredientService;
import com.team200.graduation_project.domain.ingredient.dto.request.ExtraInfoRequest;
import com.team200.graduation_project.domain.ingredient.service.IngredientFirstLoginService;
import com.team200.graduation_project.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/search")
    @Override
    public ApiResponse<?> searchIngredients(@RequestParam String keyword) {
        return ApiResponse.onSuccess(ingredientService.searchIngredients(keyword));
    }

    @PostMapping("/first/login")
    @Override
    public ApiResponse<String> saveExtraInfo(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ExtraInfoRequest request
    ) {
        return ApiResponse.onSuccess(ingredientFirstLoginService.saveExtraInfo(authorizationHeader, request));
    }
}
