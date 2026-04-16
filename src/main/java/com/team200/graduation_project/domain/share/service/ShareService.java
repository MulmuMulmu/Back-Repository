package com.team200.graduation_project.domain.share.service;

import com.team200.graduation_project.domain.share.client.KakaoLocalClient;
import com.team200.graduation_project.domain.share.converter.ShareConverter;
import com.team200.graduation_project.domain.share.dto.external.KakaoAddressResponse;
import com.team200.graduation_project.domain.share.dto.request.LocationRequest;
import com.team200.graduation_project.domain.share.dto.request.ShareRequestDTO;
import com.team200.graduation_project.domain.share.dto.response.LocationResponse;
import com.team200.graduation_project.domain.share.entity.Share;
import com.team200.graduation_project.domain.share.entity.SharePicture;
import com.team200.graduation_project.domain.share.exception.ShareErrorCode;
import com.team200.graduation_project.domain.share.exception.ShareException;
import com.team200.graduation_project.domain.share.repository.SharePictureRepository;
import com.team200.graduation_project.domain.share.repository.ShareRepository;
import com.team200.graduation_project.domain.user.entity.Location;
import com.team200.graduation_project.domain.user.entity.User;
import com.team200.graduation_project.domain.user.repository.LocationRepository;
import com.team200.graduation_project.domain.user.repository.UserRepository;
import com.team200.graduation_project.global.apiPayload.code.GeneralErrorCode;
import com.team200.graduation_project.global.apiPayload.exception.GeneralException;
import com.team200.graduation_project.global.jwt.JwtTokenProvider;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final KakaoLocalClient kakaoLocalClient;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ShareRepository shareRepository;
    private final SharePictureRepository sharePictureRepository;
    private final ShareConverter shareConverter;
    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    @Transactional
    public LocationResponse addLocation(String authorizationHeader, LocationRequest request) {
        // 1. User identification from token
        String token = extractAccessToken(authorizationHeader);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }
        String userId = jwtTokenProvider.getSubject(token);
        User user = userRepository.findByUserIdIsAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.UNAUTHORIZED));

        // 2. Fetch address from Kakao API
        KakaoAddressResponse response = kakaoLocalClient.coord2address(request.getLongitude(), request.getLatitude());

        if (response == null || response.getDocuments() == null || response.getDocuments().isEmpty()) {
            throw new GeneralException(GeneralErrorCode.LOCATION_FETCH_FAILED);
        }

        KakaoAddressResponse.Document document = response.getDocuments().get(0);

        String fullAddress = "";
        String displayAddress = "";

        if (document.getAddress() != null) {
            fullAddress = document.getAddress().getAddressName();
            displayAddress = document.getAddress().getRegion3DepthName();
        } else if (document.getRoadAddress() != null) {
            fullAddress = document.getRoadAddress().getAddressName();
            displayAddress = document.getRoadAddress().getRegion3DepthName();
        }

        if (fullAddress.isEmpty()) {
            throw new GeneralException(GeneralErrorCode.LOCATION_FETCH_FAILED);
        }

        // 3. Save or Update Location entity
        Location location = locationRepository.findByUser(user).orElse(null);
        if (location == null) {
            location = Location.builder()
                    .user(user)
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .fullAddress(fullAddress)
                    .displayAddress(displayAddress)
                    .build();
        } else {
            location.update(request.getLatitude(), request.getLongitude(), fullAddress, displayAddress);
        }
        locationRepository.save(location);

        return LocationResponse.builder()
                .full_address(fullAddress)
                .display_address(displayAddress)
                .build();
    }

    @Transactional
    public void publishSharePosting(String authorizationHeader, ShareRequestDTO request) {
        // 1. User identification from token
        User user = findUserFromHeader(authorizationHeader);

        try {
            // 2. Map to Share entity and save
            Share share = shareConverter.toShare(request, user);
            shareRepository.save(share);

            // 3. Upload images and save SharePicture entities
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                for (MultipartFile file : request.getImage()) {
                    String url = uploadToGcp(file);
                    SharePicture picture = shareConverter.toSharePicture(url, share);
                    sharePictureRepository.save(picture);
                }
            }
        } catch (Exception e) {
            throw new ShareException(ShareErrorCode.SHARE_POSTING_FAILED);
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

    private String uploadToGcp(MultipartFile file) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "_" + file.getOriginalFilename();
        String contentType = file.getContentType();

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
                .setContentType(contentType)
                .build();

        storage.create(blobInfo, file.getBytes());

        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
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
