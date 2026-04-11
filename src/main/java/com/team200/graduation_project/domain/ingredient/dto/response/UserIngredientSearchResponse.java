package com.team200.graduation_project.domain.ingredient.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class UserIngredientSearchResponse {
    private Integer sortRank;
    private String ingredient;
    private Long dDay;
    private LocalDate expirationDate;
}
