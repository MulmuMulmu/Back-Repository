package com.team200.graduation_project.domain.share.entity;

import com.team200.graduation_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "`ChatRoomParticipant`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ChatRoomParticipantId.class)
public class ChatRoomParticipant {
    @Id
    private Long chatRoomId;

    @Id
    private Long recieverId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoomId", insertable = false, updatable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recieverId", insertable = false, updatable = false)
    private User receiver;

    private LocalDateTime createTime;

}


