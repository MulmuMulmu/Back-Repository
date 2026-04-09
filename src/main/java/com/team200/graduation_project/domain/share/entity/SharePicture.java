package com.team200.graduation_project.domain.share.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`SharePicture`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SharePicture {
    @Id
    private Long sharePictureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shareId")
    private Share share;

    @Column(nullable = false, length = 250)
    private String pictureUrl;
}
