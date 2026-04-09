package com.team200.graduation_project.domain.ingredient.controller;

import com.team200.graduation_project.domain.ingredient.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;
}
