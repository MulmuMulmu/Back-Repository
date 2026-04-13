package com.team200.graduation_project.domain.recipe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "`Recipe`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recipe {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID recipeId;

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