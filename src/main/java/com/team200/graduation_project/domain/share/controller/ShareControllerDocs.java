package com.team200.graduation_project.domain.share.controller;

import com.team200.graduation_project.domain.share.dto.request.LocationRequest;
import com.team200.graduation_project.domain.share.dto.response.LocationResponse;
import com.team200.graduation_project.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Share", description = "나눔 도메인 API")
public interface ShareControllerDocs {

        @Operation(summary = "위치 등록 (주소 변환)", description = "위도와 경도를 입력받아 카카오 API를 통해 실제 주소(지번)를 반환합니다.")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "success": true,
                                          "result": {
                                            "full_address": "서울특별시 중구 태평로1가 31",
                                            "display_address": "태평로1가"
                                          }
                                        }
                                        """))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "위치 불러오기 실패", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                                        {
                                          "success": false,
                                          "code": "COMMON500",
                                          "result": "위치를 불러올 수 없습니다."
                                        }
                                        """)))
        })
        ApiResponse<LocationResponse> addLocation(
                        @Parameter(description = "JWT access token", required = true, example = "exampleToken") @RequestHeader("Authorization") String authorizationHeader,
                        @RequestBody(required = true, content = @Content(schema = @Schema(implementation = LocationRequest.class), examples = @ExampleObject(value = """
                                        {
                                          "latitude": 37.5665,
                                          "longitude": 126.9780
                                        }
                                        """))) LocationRequest request);
}
