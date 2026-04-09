package com.team200.graduation_project.domain.ingredient.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`Ingredient`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ingredient {
    @Id
    private Long ingredientId;

    @Column(length = 100)
    private String ingredientName;

    @Column(length = 50)
    private String category;
}
