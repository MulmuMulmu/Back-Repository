package com.team200.graduation_project.domain.recipe.controller;

import com.team200.graduation_project.domain.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
}
