package com.team200.graduation_project.domain.share.entity;

import com.team200.graduation_project.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "`Report`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {
    @Id
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporterId")
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shareId")
    private Share share;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    private LocalDateTime createTime;
}
