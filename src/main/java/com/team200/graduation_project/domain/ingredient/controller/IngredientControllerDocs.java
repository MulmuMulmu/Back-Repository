package com.team200.graduation_project.domain.ingredient.controller;

import com.team200.graduation_project.domain.ingredient.dto.request.ExtraInfoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Ingredient", description = "재료 검색/첫 로그인 추가정보 API")
public interface IngredientControllerDocs {

    @Operation(
            summary = "재료 검색",
            description = "keyword로 재료명을 부분 검색하여 상위 10개를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "result": {
                                                "ingredientNames": ["계란", "우유", "밀가루"]
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "code": "COMMON400",
                                              "result": "잘못된 요청입니다."
                                            }
                                            """
                            )
                    )
            )
    })
    com.team200.graduation_project.global.apiPayload.ApiResponse<?> searchIngredients(
            @Parameter(description = "검색 키워드", required = true, example = "계")
            @RequestParam String keyword
    );

    @Operation(
            summary = "첫 로그인 추가정보 저장",
            description = """
                    첫 로그인 시 알레르기/선호/비선호 재료를 저장합니다.
                    - allergies: 알레르기 재료명 리스트
                    - prefer_ingredients: 선호 재료명 리스트
                    - disprefer_ingredients: 비선호 재료명 리스트
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "result": "성공적으로 저장되었습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 필요",
                    content = @Content(mediaType = "application/json")
            )
    })
    com.team200.graduation_project.global.apiPayload.ApiResponse<String> saveExtraInfo(
            @Parameter(
                    description = "JWT access token (Bearer prefix 포함 가능)",
                    required = true,
                    example = "Bearer exampleToken"
            )
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ExtraInfoRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "allergies": ["계란"],
                                              "prefer_ingredients": ["우유"],
                                              "disprefer_ingredients": ["밀가루"]
                                            }
                                            """
                            )
                    )
            )
            ExtraInfoRequest request
    );
}

