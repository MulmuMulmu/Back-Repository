package com.team200.graduation_project.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`User`")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @Column(name = "userId", nullable = false, length = 50)
    private String userId;

    @Column(length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickName;

    @Column(length = 20)
    private Boolean firstLogin;

    private LocalDateTime deletedAt;

    @Column(length = 20)
    private String status;

    private Long reportCount;


    public void updateFirstLogin(Boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

}
