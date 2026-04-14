package com.team200.graduation_project.domain.admin.controller;

import com.team200.graduation_project.domain.admin.dto.request.AdminIngredientRequest;
import com.team200.graduation_project.domain.admin.service.AdminService;
import com.team200.graduation_project.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController implements AdminControllerDocs {

    private final AdminService adminService;

    @Override
    @PostMapping("/ingredient/input")
    public ApiResponse<String> addIngredients(@RequestBody List<AdminIngredientRequest> requests) {
        try {
            adminService.addIngredients(requests);
            return ApiResponse.onSuccess("식재료가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            return ApiResponse.onFailure("COMMON500", "식재료를 등록할 수 없습니다.");
        }
    }
}
