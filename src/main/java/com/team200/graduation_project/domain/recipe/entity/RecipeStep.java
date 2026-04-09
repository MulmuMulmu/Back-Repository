package com.team200.graduation_project.domain.recipe.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`RecipeStep`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeStep {
    @Id
    private Long recipeStepId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId")
    private Recipe recipe;

    private Integer stepOrder;

    @Column(columnDefinition = "TEXT")
    private String description;
}