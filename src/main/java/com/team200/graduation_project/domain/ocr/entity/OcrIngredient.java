package com.team200.graduation_project.domain.ocr.entity;

import com.team200.graduation_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OcrIngredient {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID ocrIngredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ocrId")
    private Ocr ocr;

    @Column(length = 100)
    private String ocrIngredientName;

    private Integer quantity;

}
