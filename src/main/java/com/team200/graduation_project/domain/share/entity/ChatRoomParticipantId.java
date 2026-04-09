package com.team200.graduation_project.domain.share.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public class ChatRoomParticipantId implements Serializable {
        private Long chatRoomId;
        private Long recieverId;
    }
