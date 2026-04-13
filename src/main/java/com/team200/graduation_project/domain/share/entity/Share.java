package com.team200.graduation_project.domain.share.entity;

import com.team200.graduation_project.domain.ingredient.entity.UserIngredient;
import com.team200.graduation_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "`Share`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Share {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID shareId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIngredientId")
    private UserIngredient userIngredient;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 20)
    private String status;

    @Column(length = 20)
    private String isView;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
