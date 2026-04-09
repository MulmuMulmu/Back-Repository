package com.team200.graduation_project.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`Location`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    @Id
    private Long locationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    private Double latitude;

    private Double longitude;

    @Column(length = 100)
    private String fullAddress;

    @Column(length = 50)
    private String displayAddress;
}
