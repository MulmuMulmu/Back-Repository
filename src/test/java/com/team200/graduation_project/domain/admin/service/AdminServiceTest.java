package com.team200.graduation_project.domain.admin.service;

import com.team200.graduation_project.domain.admin.dto.request.AdminIngredientAliasRequest;
import com.team200.graduation_project.domain.admin.dto.request.AdminOcrIngredientUpdateRequest;
import com.team200.graduation_project.domain.ingredient.entity.Ingredient;
import com.team200.graduation_project.domain.ingredient.entity.IngredientAlias;
import com.team200.graduation_project.domain.ingredient.repository.IngredientAliasRepository;
import com.team200.graduation_project.domain.ingredient.repository.IngredientRepository;
import com.team200.graduation_project.domain.ingredient.repository.UserIngredientRepository;
import com.team200.graduation_project.domain.ocr.repository.OcrIngredientRepository;
import com.team200.graduation_project.domain.ocr.repository.OcrRepository;
import com.team200.graduation_project.domain.ocr.entity.Ocr;
import com.team200.graduation_project.domain.ocr.entity.OcrIngredient;
import com.team200.graduation_project.domain.share.repository.ReportRepository;
import com.team200.graduation_project.domain.share.repository.ShareRepository;
import com.team200.graduation_project.domain.user.repository.UserRepository;
import com.team200.graduation_project.global.jwt.JwtTokenService;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private IngredientAliasRepository ingredientAliasRepository;
    @Mock
    private UserIngredientRepository userIngredientRepository;
    @Mock
    private OcrRepository ocrRepository;
    @Mock
    private OcrIngredientRepository ocrIngredientRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private ShareRepository shareRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void addIngredientAliasSavesAliasToCanonicalIngredient() {
        Ingredient milk = Ingredient.builder()
                .ingredientName("우유")
                .category("유제품")
                .build();
        when(ingredientAliasRepository.findByNormalizedAliasName("맛있는우유gt")).thenReturn(Optional.empty());
        when(ingredientRepository.findByIngredientName("우유")).thenReturn(Optional.of(milk));

        String result = adminService.addIngredientAlias(AdminIngredientAliasRequest.builder()
                .aliasName("맛있는우유GT")
                .ingredientName("우유")
                .source("admin_review")
                .build());

        assertThat(result).isEqualTo("식재료 별칭이 성공적으로 등록되었습니다.");
        ArgumentCaptor<IngredientAlias> captor = ArgumentCaptor.forClass(IngredientAlias.class);
        verify(ingredientAliasRepository).save(captor.capture());
        assertThat(captor.getValue().getAliasName()).isEqualTo("맛있는우유GT");
        assertThat(captor.getValue().getNormalizedAliasName()).isEqualTo("맛있는우유gt");
        assertThat(captor.getValue().getIngredient()).isEqualTo(milk);
        assertThat(captor.getValue().getSource()).isEqualTo("admin_review");
    }

    @Test
    void addIngredientAliasDoesNotSaveDuplicateNormalizedAlias() {
        Ingredient milk = Ingredient.builder()
                .ingredientName("우유")
                .category("유제품")
                .build();
        IngredientAlias existingAlias = IngredientAlias.builder()
                .aliasName("맛있는우유GT")
                .normalizedAliasName("맛있는우유gt")
                .ingredient(milk)
                .source("admin_review")
                .build();
        when(ingredientAliasRepository.findByNormalizedAliasName("맛있는우유gt"))
                .thenReturn(Optional.of(existingAlias));

        String result = adminService.addIngredientAlias(AdminIngredientAliasRequest.builder()
                .aliasName("맛있는우유GT")
                .ingredientName("우유")
                .source("admin_review")
                .build());

        assertThat(result).isEqualTo("식재료 별칭이 이미 등록되어 있습니다.");
        verify(ingredientAliasRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateOcrIngredientsRegistersAliasWhenAdminCorrectsRawProductToCanonicalIngredient() {
        UUID ocrId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID ocrIngredientId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        Ocr ocr = Ocr.builder()
                .ocrId(ocrId)
                .build();
        OcrIngredient ocrIngredient = OcrIngredient.builder()
                .ocrIngredientId(ocrIngredientId)
                .ocr(ocr)
                .ocrIngredientName("초이스엘우유팩")
                .quantity(1)
                .build();
        Ingredient milk = Ingredient.builder()
                .ingredientName("우유")
                .category("유제품")
                .build();
        when(ocrRepository.findById(ocrId)).thenReturn(Optional.of(ocr));
        when(ocrIngredientRepository.findById(ocrIngredientId)).thenReturn(Optional.of(ocrIngredient));
        when(ingredientAliasRepository.findByNormalizedAliasName("초이스엘우유팩")).thenReturn(Optional.empty());
        when(ingredientRepository.findByIngredientName("우유")).thenReturn(Optional.of(milk));

        String result = adminService.updateOcrIngredients(AdminOcrIngredientUpdateRequest.builder()
                .ocrId(ocrId)
                .items(List.of(AdminOcrIngredientUpdateRequest.Item.builder()
                        .ocrIngredientId(ocrIngredientId)
                        .itemName("우유")
                        .quantity(1)
                        .build()))
                .build());

        assertThat(result).isEqualTo("OCR 품목이 수정 완료되었습니다.");
        assertThat(ocrIngredient.getOcrIngredientName()).isEqualTo("우유");
        ArgumentCaptor<IngredientAlias> captor = ArgumentCaptor.forClass(IngredientAlias.class);
        verify(ingredientAliasRepository).save(captor.capture());
        assertThat(captor.getValue().getAliasName()).isEqualTo("초이스엘우유팩");
        assertThat(captor.getValue().getIngredient()).isEqualTo(milk);
        assertThat(captor.getValue().getSource()).isEqualTo("ocr_admin_review");
    }
}
