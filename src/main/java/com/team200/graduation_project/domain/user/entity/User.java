package com.team200.graduation_project.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`User`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    private Long userId;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickName;

    @Column(length = 20)
    private Boolean firstLogin;
}
