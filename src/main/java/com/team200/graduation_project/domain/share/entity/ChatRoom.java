package com.team200.graduation_project.domain.share.entity;

import com.team200.graduation_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "`ChatRoom`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {
    @Id
    private Long chatRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shareId")
    private Share share;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senderId")
    private User sender;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
