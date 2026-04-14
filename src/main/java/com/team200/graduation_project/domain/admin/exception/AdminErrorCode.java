package com.team200.graduation_project.domain.admin.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AdminErrorCode {





    private final HttpStatus status;
    private final String code;
    private final String message;
}
