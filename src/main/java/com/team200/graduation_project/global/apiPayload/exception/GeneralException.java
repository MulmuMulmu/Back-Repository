package com.team200.graduation_project.global.apiPayload.exception;

import com.team200.graduation_project.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private final BaseErrorCode errorCode;

}
