package com.team200.graduation_project.domain.ocr.entity;

import com.team200.graduation_project.domain.ingredient.entity.Ingredient;
import com.team200.graduation_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.Length;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "`OcrIngredient`")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OcrIngredient {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID ocrIngredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ocrId")
    private Ocr ocrId;

    @Column(length = 100)
    private String ocrIngredientName;

}
