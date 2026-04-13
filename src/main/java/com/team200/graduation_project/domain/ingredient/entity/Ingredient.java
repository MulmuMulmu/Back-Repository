package com.team200.graduation_project.domain.ingredient.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "`Ingredient`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ingredient {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID ingredientId;

    @Column(length = 100)
    private String ingredientName;

    @Column(length = 50)
    private String category;
}
