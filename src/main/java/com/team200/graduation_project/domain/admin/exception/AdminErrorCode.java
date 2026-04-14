package com.team200.graduation_project.domain.admin.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AdminErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "COMMON500", "예기치 않은 서버 에러가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
