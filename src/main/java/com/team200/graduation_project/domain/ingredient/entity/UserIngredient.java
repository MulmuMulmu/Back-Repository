package com.team200.graduation_project.domain.ingredient.entity;


import com.team200.graduation_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "`UserIngredient`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserIngredient {
    @Id
    private Long userIngredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientId")
    private Ingredient ingredient;

    private Double amount;

    @Column(length = 20)
    private String unit;

    private LocalDate purchaseDate;

    private LocalDate expirationDate;

    @Column(length = 20)
    private String status;
}
