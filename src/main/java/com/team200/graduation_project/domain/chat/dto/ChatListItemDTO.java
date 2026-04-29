package com.team200.graduation_project.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatListItemDTO {
    private String senderNicName;
    private String lastMessage;
    private String sendTime;
    private String type;
}
