package com.team200.graduation_project.domain.recipe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`Recipe`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recipe {
    @Id
    private Long recipeId;

    @Column(length = 100)
    private String name;

    @Column(length = 50)
    private String category;

    @Column(length = 50)
    private String cookingMethod;

    private Long calories;

    @Column(length = 200)
    private String imageUrl;
}