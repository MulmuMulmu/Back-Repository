package com.team200.graduation_project.domain.share.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ShareErrorCode {

    SHARE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "SHARE400", "잘못된 요청입니다."),
    SHARE_NOT_FOUND(HttpStatus.NOT_FOUND, "SHARE404", "리소스를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
