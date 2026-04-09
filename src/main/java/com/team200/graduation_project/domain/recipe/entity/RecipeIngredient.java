package com.team200.graduation_project.domain.recipe.entity;

import com.team200.graduation_project.domain.ingredient.entity.Ingredient;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`RecipeIngredient`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeIngredient {
    @Id
    private Long recipeIngredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientId")
    private Ingredient ingredient;

    private Double amount;

    @Column(length = 20)
    private String unit;
}