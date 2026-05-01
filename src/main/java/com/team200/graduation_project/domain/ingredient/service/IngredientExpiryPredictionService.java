package com.team200.graduation_project.domain.ingredient.service;

import com.team200.graduation_project.domain.ai.client.AiClient;
import com.team200.graduation_project.domain.ai.client.AiClientException;
import com.team200.graduation_project.domain.ai.dto.ExpiryPredictionRequest;
import com.team200.graduation_project.domain.ai.dto.ExpiryPredictionResult;
import com.team200.graduation_project.domain.ingredient.entity.Ingredient;
import com.team200.graduation_project.domain.ingredient.entity.IngredientExpiryRule;
import com.team200.graduation_project.domain.ingredient.repository.IngredientExpiryRuleRepository;
import com.team200.graduation_project.domain.ingredient.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientExpiryPredictionService {

    private static final String DEFAULT_STORAGE_METHOD = "냉장";
    private static final String AI_SOURCE = "AI";

    private final IngredientRepository ingredientRepository;
    private final IngredientExpiryRuleRepository expiryRuleRepository;
    private final AiClient aiClient;

    @Transactional
    public ExpiryPredictionResult predict(ExpiryPredictionRequest request) {
        if (request == null || request.purchaseDate() == null || request.ingredients() == null) {
            throw new AiClientException("Invalid expiry prediction request.");
        }

        LocalDate purchaseDate = parseDate(request.purchaseDate());
        List<ExpiryPredictionResult.IngredientExpiry> predictions = new ArrayList<>();

        for (String rawName : request.ingredients()) {
            String ingredientName = normalizeIngredientName(rawName);
            if (ingredientName.isBlank()) {
                continue;
            }

            Ingredient ingredient = ingredientRepository.findByIngredientName(ingredientName)
                    .orElseThrow(() -> new AiClientException("Unknown ingredient: " + ingredientName));

            IngredientExpiryRule rule = expiryRuleRepository
                    .findByIngredientIngredientIdAndStorageMethod(ingredient.getIngredientId(), DEFAULT_STORAGE_METHOD)
                    .orElseGet(() -> createRuleFromAi(ingredient, purchaseDate));

            predictions.add(new ExpiryPredictionResult.IngredientExpiry(
                    ingredientName,
                    purchaseDate.plusDays(rule.getShelfLifeDays()).toString()
            ));
        }

        return new ExpiryPredictionResult(request.purchaseDate(), predictions);
    }

    private IngredientExpiryRule createRuleFromAi(Ingredient ingredient, LocalDate purchaseDate) {
        ExpiryPredictionResult aiResult = aiClient.predictExpiry(new ExpiryPredictionRequest(
                purchaseDate.toString(),
                List.of(ingredient.getIngredientName())
        ));

        String expirationDate = aiResult.ingredients().stream()
                .filter(result -> ingredient.getIngredientName().equals(result.ingredientName()))
                .findFirst()
                .or(() -> aiResult.ingredients().stream().findFirst())
                .map(ExpiryPredictionResult.IngredientExpiry::expirationDate)
                .orElseThrow(() -> new AiClientException("AI expiry prediction result is empty."));

        long shelfLifeDays = ChronoUnit.DAYS.between(purchaseDate, parseDate(expirationDate));
        if (shelfLifeDays < 0 || shelfLifeDays > 3650) {
            throw new AiClientException("AI expiry prediction result is out of range.");
        }

        return expiryRuleRepository.save(IngredientExpiryRule.builder()
                .ingredient(ingredient)
                .storageMethod(DEFAULT_STORAGE_METHOD)
                .shelfLifeDays((int) shelfLifeDays)
                .source(AI_SOURCE)
                .confidence(1.0)
                .build());
    }

    private String normalizeIngredientName(String value) {
        return value == null ? "" : value.trim();
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new AiClientException("Invalid date: " + value, e);
        }
    }
}
