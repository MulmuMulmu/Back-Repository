package com.team200.graduation_project.global.apiPayload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.team200.graduation_project.global.apiPayload.code.BaseErrorCode;
import com.team200.graduation_project.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"success", "code", "message", "data"})
public class ApiResponse<T> {
    @JsonProperty("success")
    private final Boolean success;

    @JsonProperty("code")
    private final String code;

    @JsonProperty("message")
    private final String message;

    @JsonProperty("data")
    private T data;

    public static <T> ApiResponse<T> onSuccess(BaseSuccessCode code, T data) {
        return new ApiResponse<>(true, code.getCode(), code.getMessage(), data);
    }


    public static <T> ApiResponse<T> onFailure(BaseErrorCode code, T data) {
        return new ApiResponse<>(false, code.getCode(), code.getMessage(), data);
    }
}
