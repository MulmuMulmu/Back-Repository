package com.team200.graduation_project.domain.ingredient.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserIngredientSearchRequest {
    private List<String> category;
    private String sort;
}
