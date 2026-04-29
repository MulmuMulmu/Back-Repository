package com.team200.graduation_project.domain.chat.service;

import com.team200.graduation_project.domain.chat.converter.ChatConverter;
import com.team200.graduation_project.domain.chat.dto.*;
import com.team200.graduation_project.domain.share.entity.ChatMessage;
import com.team200.graduation_project.domain.share.entity.ChatRoom;
import com.team200.graduation_project.domain.share.entity.ChatRoomParticipant;
import com.team200.graduation_project.domain.share.entity.ChatRoomParticipantId;
import com.team200.graduation_project.domain.share.entity.Share;
import com.team200.graduation_project.domain.share.exception.ShareErrorCode;
import com.team200.graduation_project.domain.share.exception.ShareException;
import com.team200.graduation_project.domain.share.repository.ChatMessageRepository;
import com.team200.graduation_project.domain.share.repository.ChatRoomParticipantRepository;
import com.team200.graduation_project.domain.share.repository.ChatRoomRepository;
import com.team200.graduation_project.domain.share.repository.ShareRepository;
import com.team200.graduation_project.domain.user.entity.User;
import com.team200.graduation_project.domain.user.repository.UserRepository;
import com.team200.graduation_project.global.apiPayload.code.GeneralErrorCode;
import com.team200.graduation_project.global.apiPayload.exception.GeneralException;
import com.team200.graduation_project.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ShareRepository shareRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatConverter chatConverter;

    @Transactional
    public ChatStartResponseDTO startChat(String authorizationHeader, ChatStartRequestDTO request) {
        User sender = findUserFromHeader(authorizationHeader);

        Share share = shareRepository.findById(request.getPostId())
                .orElseThrow(() -> new ShareException(ShareErrorCode.SHARE_POSTING_NOT_FOUND));

        if (share.getUser().getUserId().equals(sender.getUserId())) {
            throw new GeneralException(GeneralErrorCode.BAD_REQUEST);
        }

        Optional<ChatRoom> existingRoom = chatRoomRepository.findByShareAndSender(share, sender);
        if (existingRoom.isPresent()) {
            return chatConverter.toChatStartResponseDTO(existingRoom.get());
        }

        try {
            ChatRoom chatRoom = ChatRoom.builder()
                    .share(share)
                    .sender(sender)
                    .build();
            chatRoom = chatRoomRepository.save(chatRoom);

            ChatRoomParticipant participant = ChatRoomParticipant.builder()
                    .chatRoomId(chatRoom.getChatRoomId())
                    .receiverId(share.getUser().getUserId())
                    .build();
            chatRoomParticipantRepository.save(participant);

            return chatConverter.toChatStartResponseDTO(chatRoom);
        } catch (Exception e) {
            throw new GeneralException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public List<ChatListItemDTO> getChatList(String authorizationHeader, String type) {
        User user = findUserFromHeader(authorizationHeader);

        List<ChatListItemDTO> resultList = new ArrayList<>();

        if ("all".equalsIgnoreCase(type) || "take".equalsIgnoreCase(type)) {
            List<ChatRoom> takeRooms = chatRoomRepository.findAllBySender(user);
            takeRooms.forEach(room -> {
                ChatMessage lastMsg = chatMessageRepository.findFirstByChatRoomOrderByCreateTimeDesc(room).orElse(null);
                User opponent = room.getShare().getUser();
                resultList.add(chatConverter.toChatListItemDTO(room, lastMsg, "take", opponent));
            });
        }

        if ("all".equalsIgnoreCase(type) || "give".equalsIgnoreCase(type)) {
            List<ChatRoom> giveRooms = chatRoomRepository.findAllByShare_User(user);
            giveRooms.forEach(room -> {
                ChatMessage lastMsg = chatMessageRepository.findFirstByChatRoomOrderByCreateTimeDesc(room).orElse(null);
                User opponent = room.getSender();
                resultList.add(chatConverter.toChatListItemDTO(room, lastMsg, "give", opponent));
            });
        }

        resultList.sort((a, b) -> b.getSendTime().compareTo(a.getSendTime()));

        return resultList;
    }

    @Transactional(readOnly = true)
    public List<ChatReceptionResponseDTO.MessageItemDTO> getChatMessages(String authorizationHeader, UUID chatRoomId) {
        User user = findUserFromHeader(authorizationHeader);
        String userId = user.getUserId();

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND));

        boolean isInitiator = chatRoom.getSender().getUserId().equals(userId);
        boolean isReceiver = chatRoomParticipantRepository.existsById(new ChatRoomParticipantId(chatRoomId, userId));

        if (!isInitiator && !isReceiver) {
            throw new GeneralException(GeneralErrorCode.FORBIDDEN);
        }

        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoom_ChatRoomIdOrderByCreateTimeAsc(chatRoomId);

        return messages.stream()
                .map(chatConverter::toMessageItemDTO)
                .toList();
    }

    @Transactional
    public void sendMessage(String authorizationHeader, ChatMessageRequestDTO request) {
        User sender = findUserFromHeader(authorizationHeader);
        String senderId = sender.getUserId();

        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND));

        boolean isInitiator = chatRoom.getSender().getUserId().equals(senderId);
        boolean isReceiver = chatRoomParticipantRepository.existsById(new ChatRoomParticipantId(request.getChatRoomId(), senderId));

        if (!isInitiator && !isReceiver) {
            throw new GeneralException(GeneralErrorCode.FORBIDDEN);
        }

        try {
            ChatMessage message = ChatMessage.builder()
                    .user(sender)
                    .chatRoom(chatRoom)
                    .type("TALK")
                    .detailMessage(request.getContent())
                    .build();
            chatMessageRepository.save(message);
        } catch (Exception e) {
            throw new GeneralException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void markAsRead(String authorizationHeader, ChatReadRequestDTO request) {
        User user = findUserFromHeader(authorizationHeader);
        String userId = user.getUserId();
        UUID chatRoomId = request.getChatRoomId();

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND));

        boolean isInitiator = chatRoom.getSender().getUserId().equals(userId);
        boolean isReceiver = chatRoomParticipantRepository.existsById(new ChatRoomParticipantId(chatRoomId, userId));

        if (!isInitiator && !isReceiver) {
            throw new GeneralException(GeneralErrorCode.FORBIDDEN);
        }

        try {
            chatMessageRepository.markMessagesAsRead(chatRoomId, userId);
        } catch (Exception e) {
            throw new GeneralException(GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private User findUserFromHeader(String authorizationHeader) {
        String token = extractAccessToken(authorizationHeader);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }
        String userId = jwtTokenProvider.getSubject(token);
        return userRepository.findByUserIdIsAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.UNAUTHORIZED));
    }

    private String extractAccessToken(String authorizationHeader) {
        if (!StringUtils.hasText(authorizationHeader)) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }
        String bearerPrefix = "Bearer ";
        if (authorizationHeader.startsWith(bearerPrefix)) {
            return authorizationHeader.substring(bearerPrefix.length()).trim();
        }
        return authorizationHeader.trim();
    }
}
