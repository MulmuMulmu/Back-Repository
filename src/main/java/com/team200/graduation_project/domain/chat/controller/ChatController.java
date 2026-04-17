package com.team200.graduation_project.domain.chat.controller;

import com.team200.graduation_project.domain.chat.dto.*;
import com.team200.graduation_project.domain.chat.service.ChatService;
import com.team200.graduation_project.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController implements ChatControllerDocs {

    private final ChatService chatService;

    @Override
    @PostMapping
    public ApiResponse<ChatStartResponseDTO> startChat(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ChatStartRequestDTO request
    ) {
        return ApiResponse.onSuccess(chatService.startChat(authorizationHeader, request));
    }

    @Override
    @PostMapping("/sending")
    public ApiResponse<String> sendMessage(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ChatMessageRequestDTO request
    ) {
        chatService.sendMessage(authorizationHeader, request);
        return ApiResponse.onSuccess("메시지가 성공적으로 전송되었습니다.");
    }

    @Override
    @GetMapping("/reception")
    public ApiResponse<List<ChatReceptionResponseDTO.MessageItemDTO>> getChatMessages(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam UUID chatRoomId
    ) {
        return ApiResponse.onSuccess(chatService.getChatMessages(authorizationHeader, chatRoomId));
    }

    @Override
    @PatchMapping("/read")
    public ApiResponse<String> markAsRead(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ChatReadRequestDTO request
    ) {
        chatService.markAsRead(authorizationHeader, request);
        return ApiResponse.onSuccess("채팅방의 모든 메시지가 읽음 처리되었습니다.");
    }

    @Override
    @GetMapping("/list")
    public ApiResponse<List<ChatListItemDTO>> getChatList(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam String type
    ) {
        return ApiResponse.onSuccess(chatService.getChatList(authorizationHeader, type));
    }
}
