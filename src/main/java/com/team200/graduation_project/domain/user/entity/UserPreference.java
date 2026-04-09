package com.team200.graduation_project.domain.user.entity;

import com.team200.graduation_project.domain.ingredient.entity.Ingredient;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`UserPreference`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPreference {
    @Id
    private Long userPreferenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientId")
    private Ingredient ingredient;

    @Column(length = 50)
    private String type;
}
