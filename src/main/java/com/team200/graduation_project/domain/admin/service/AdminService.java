package com.team200.graduation_project.domain.admin.service;

import com.team200.graduation_project.domain.admin.dto.request.AdminIngredientRequest;
import com.team200.graduation_project.domain.ingredient.entity.Ingredient;
import com.team200.graduation_project.domain.ingredient.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final IngredientRepository ingredientRepository;

    public void addIngredients(List<AdminIngredientRequest> requests) {
        List<Ingredient> ingredients = requests.stream()
                .map(request -> Ingredient.builder()
                        .ingredientName(request.getIngredient())
                        .category(request.getCategory())
                        .build())
                .collect(Collectors.toList());

        ingredientRepository.saveAll(ingredients);
    }
}
