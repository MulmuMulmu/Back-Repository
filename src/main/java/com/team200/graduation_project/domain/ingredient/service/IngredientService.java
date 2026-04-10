package com.team200.graduation_project.domain.ingredient.service;

import com.team200.graduation_project.domain.ingredient.dto.response.IngredientSearchResponse;
import com.team200.graduation_project.domain.ingredient.repository.IngredientRepository;
import com.team200.graduation_project.global.apiPayload.code.GeneralErrorCode;
import com.team200.graduation_project.global.apiPayload.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    @Transactional(readOnly = true)
    public IngredientSearchResponse searchIngredients(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST);
        }

        List<String> ingredientNames = ingredientRepository.findTop10ByIngredientNameContaining(keyword.trim())
                .stream()
                .map(ingredient -> ingredient.getIngredientName())
                .toList();

        return new IngredientSearchResponse(ingredientNames);
    }
}
