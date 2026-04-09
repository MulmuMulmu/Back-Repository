package com.team200.graduation_project.domain.share.entity;

import com.team200.graduation_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "`ChatMessage`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    @Id
    private Long chatMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoomId")
    private ChatRoom chatRoom;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String detailMessage;

    private LocalDateTime createTime;
    private Boolean isRead;
}
