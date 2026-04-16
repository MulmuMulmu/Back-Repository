package com.team200.graduation_project.domain.share.converter;

import com.team200.graduation_project.domain.share.dto.request.ShareRequestDTO;
import com.team200.graduation_project.domain.share.entity.Share;
import com.team200.graduation_project.domain.share.entity.SharePicture;
import com.team200.graduation_project.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShareConverter {

    public Share toShare(ShareRequestDTO request, User user) {
        return Share.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getDescription())
                .category(request.getCategory())
                .expirationDate(request.getExpirationDate())
                .status("AVAILABLE")
                .isView("Y")
                .build();
    }

    public SharePicture toSharePicture(String pictureUrl, Share share) {
        return SharePicture.builder()
                .share(share)
                .pictureUrl(pictureUrl)
                .build();
    }
}
