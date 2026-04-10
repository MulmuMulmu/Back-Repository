package com.team200.graduation_project.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode {

    USER_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    USER_ID_DUPLICATED(HttpStatus.BAD_REQUEST, "COMMON400", "중복된 id가 이미 있습니다."),
    USER_KAKAO_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "COMMON400", "카카오 토큰 값을 불러올 수 없습니다."),
    USER_SIGNUP_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "회원가입을 완료할 수 없습니다."),
    USER_LOGIN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "로그인 할 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "리소스를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
