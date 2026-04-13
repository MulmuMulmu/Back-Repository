package com.team200.graduation_project.domain.recipe.entity;

import com.team200.graduation_project.domain.ingredient.entity.Ingredient;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "`RecipeIngredient`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeIngredient {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID recipeIngredientId;

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