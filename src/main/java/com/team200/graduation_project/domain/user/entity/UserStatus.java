package com.team200.graduation_project.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    NORMAL("정상"),
    WITHDRAWN("탈퇴"),
    BLOCKED("차단");

    private final String description;
}
