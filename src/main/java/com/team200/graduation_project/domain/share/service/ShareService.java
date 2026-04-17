package com.team200.graduation_project.domain.share.service;

import com.team200.graduation_project.domain.share.client.KakaoLocalClient;
import com.team200.graduation_project.domain.share.converter.ShareConverter;
import com.team200.graduation_project.domain.share.dto.external.KakaoAddressResponse;
import com.team200.graduation_project.domain.share.dto.request.LocationRequest;
import com.team200.graduation_project.domain.share.dto.request.ReportRequestDTO;
import com.team200.graduation_project.domain.share.dto.request.ShareRequestDTO;
import com.team200.graduation_project.domain.share.dto.request.ShareSuccessionRequestDTO;
import com.team200.graduation_project.domain.share.dto.response.LocationResponse;
import com.team200.graduation_project.domain.share.dto.response.MyShareItemDTO;
import com.team200.graduation_project.domain.share.dto.response.ShareDetailResponseDTO;
import com.team200.graduation_project.domain.share.dto.response.ShareListResponseDTO;
import com.team200.graduation_project.domain.share.entity.Report;
import com.team200.graduation_project.domain.share.entity.Share;
import com.team200.graduation_project.domain.share.entity.SharePicture;
import com.team200.graduation_project.domain.share.exception.ShareErrorCode;
import com.team200.graduation_project.domain.share.exception.ShareException;
import com.team200.graduation_project.domain.share.repository.ReportRepository;
import com.team200.graduation_project.domain.share.repository.SharePictureRepository;
import com.team200.graduation_project.domain.ingredient.entity.UserIngredient;
import com.team200.graduation_project.domain.ingredient.repository.UserIngredientRepository;
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
import java.util.Comparator;
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
    private final ReportRepository reportRepository;
    private final UserIngredientRepository userIngredientRepository;
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

        // 2. Fetch and validate UserIngredient by name
        UserIngredient userIngredient = null;
        if (StringUtils.hasText(request.getIngredientName())) {
            List<UserIngredient> matchingIngredients = userIngredientRepository.findByUserAndIngredient_IngredientName(user, request.getIngredientName());
            
            userIngredient = matchingIngredients.stream()
                    .filter(i -> i.getExpirationDate() != null)
                    .min(Comparator.comparing(UserIngredient::getExpirationDate))
                    .orElseGet(() -> matchingIngredients.stream().findFirst().orElse(null));

            if (userIngredient == null) {
                throw new ShareException(ShareErrorCode.USER_INGREDIENT_NOT_FOUND);
            }
        } else {
            // If the user didn't provide an ingredient name, throw an error as per requirement
            throw new ShareException(ShareErrorCode.SHARE_BAD_REQUEST);
        }

        try {
            // 3. Map to Share entity and save
            Share share = shareConverter.toShare(request, user, userIngredient);
            shareRepository.save(share);

            // 4. Upload image and save SharePicture entity
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                String url = uploadToGcp(request.getImage());
                SharePicture picture = shareConverter.toSharePicture(url, share);
                sharePictureRepository.save(picture);
            }
        } catch (ShareException e) {
            throw e;
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

    @Transactional(readOnly = true)
    public ShareListResponseDTO getShareList(String authorizationHeader) {
        // 1. Identify current user and their location
        User currentUser = findUserFromHeader(authorizationHeader);
        Location currentUserLocation = locationRepository.findByUser(currentUser)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.LOCATION_NOT_FOUND));

        double myLat = currentUserLocation.getLatitude();
        double myLon = currentUserLocation.getLongitude();

        // 2. Fetch all postings (with user fetched)
        List<Share> allShares = shareRepository.findAllWithUser();

        // 3. Filter by distance (10km) and calculate distance
        List<ShareWithDistance> filteredShares = allShares.stream()
                .map(share -> {
                    // 비노출 게시글 제외
                    if (java.util.Objects.equals(share.getIsView(), false)) return null;

                    // 본인의 글은 제외
                    if (share.getUser().getUserId().equals(currentUser.getUserId())) return null;

                    Location posterLocation = locationRepository.findByUser(share.getUser()).orElse(null);
                    if (posterLocation == null) return null;
                    double distance = calculateDistance(myLat, myLon, posterLocation.getLatitude(), posterLocation.getLongitude());
                    if (distance > 10.0) return null;
                    return new ShareWithDistance(share, distance, posterLocation.getDisplayAddress());
                })
                .filter(java.util.Objects::nonNull)
                .sorted((a, b) -> b.share.getCreateTime().compareTo(a.share.getCreateTime())) // 최신순
                .toList();

        long totalCount = filteredShares.size();

        // 4. Map to DTOs using directly joined SharePicture
        List<ShareListResponseDTO.ShareItemDTO> itemDTOs = filteredShares.stream()
                .map(swd -> {
                    String firstImageUrl = swd.share.getSharePicture() != null 
                            ? swd.share.getSharePicture().getPictureUrl() 
                            : null;
                    
                    return shareConverter.toShareItemDTO(swd.share, swd.distance, firstImageUrl, swd.displayAddress);
                })
                .toList();

        return shareConverter.toShareListResponse(itemDTOs, totalCount);
    }

    @Transactional(readOnly = true)
    public ShareDetailResponseDTO getShareDetail(UUID postId) {
        Share share = shareRepository.findWithUserByShareId(postId)
                .orElseThrow(() -> new ShareException(ShareErrorCode.SHARE_POSTING_NOT_FOUND));

        return shareConverter.toShareDetailResponse(share, share.getSharePicture());
    }

    @Transactional(readOnly = true)
    public List<MyShareItemDTO> getMyShareList(String authorizationHeader, String type) {
        User user = findUserFromHeader(authorizationHeader);

        String status = "AVAILABLE";
        if ("나눔 완료".equals(type)) {
            status = "COMPLETED";
        } else if ("나눔 중".equals(type)) {
            status = "AVAILABLE";
        }

        try {
            List<Share> myShares = shareRepository.findAllByUserAndStatusOrderByCreateTimeDesc(user, status);

            return myShares.stream()
                    .map(share -> {
                        String imageUrl = share.getSharePicture() != null 
                                ? share.getSharePicture().getPictureUrl() 
                                : null;
                        return shareConverter.toMyShareItemDTO(share, imageUrl);
                    })
                    .toList();
        } catch (Exception e) {
            throw new ShareException(ShareErrorCode.MY_SHARE_LIST_FETCH_FAILED);
        }
    }

    @Transactional
    public void updateMySharePosting(String authorizationHeader, UUID postId, ShareRequestDTO request) {
        User user = findUserFromHeader(authorizationHeader);
        Share share = shareRepository.findById(postId)
                .orElseThrow(() -> new ShareException(ShareErrorCode.SHARE_POSTING_NOT_FOUND));

        if (!share.getUser().getUserId().equals(user.getUserId())) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }

        // Fetch and validate UserIngredient if name provided
        UserIngredient userIngredient = share.getUserIngredient();
        if (StringUtils.hasText(request.getIngredientName())) {
            List<UserIngredient> matchingIngredients = userIngredientRepository.findByUserAndIngredient_IngredientName(user, request.getIngredientName());

            userIngredient = matchingIngredients.stream()
                    .filter(i -> i.getExpirationDate() != null)
                    .min(Comparator.comparing(UserIngredient::getExpirationDate))
                    .orElseGet(() -> matchingIngredients.stream().findFirst().orElse(null));

            if (userIngredient == null) {
                throw new ShareException(ShareErrorCode.USER_INGREDIENT_NOT_FOUND);
            }
        }

        try {
            share.update(request.getTitle(), request.getContent(), request.getCategory(), request.getExpirationDate(), userIngredient);
            shareRepository.save(share);

            // Update image if a new file is uploaded
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                String imageUrl = uploadToGcp(request.getImage());
                if (share.getSharePicture() != null) {
                    share.getSharePicture().updateUrl(imageUrl);
                } else {
                    SharePicture newPicture = shareConverter.toSharePicture(imageUrl, share);
                    sharePictureRepository.save(newPicture);
                }
            }
        } catch (ShareException e) {
            throw e;
        } catch (Exception e) {
            throw new ShareException(ShareErrorCode.SHARE_POSTING_FAILED);
        }
    }

    @Transactional
    public void deleteMySharePosting(String authorizationHeader, UUID postId) {
        User user = findUserFromHeader(authorizationHeader);
        Share share = shareRepository.findById(postId)
                .orElseThrow(() -> new ShareException(ShareErrorCode.SHARE_POSTING_NOT_FOUND));

        if (!share.getUser().getUserId().equals(user.getUserId())) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }

        try {
            share.softDelete();
            shareRepository.save(share);
        } catch (Exception e) {
            throw new ShareException(ShareErrorCode.SHARE_POSTING_FAILED);
        }
    }

    @Transactional
    public void reportSharePosting(String authorizationHeader, UUID postId, ReportRequestDTO request) {
        User reporter = findUserFromHeader(authorizationHeader);
        Share share = shareRepository.findById(postId)
                .orElseThrow(() -> new ShareException(ShareErrorCode.SHARE_POSTING_NOT_FOUND));

        try {
            Report report = shareConverter.toReport(request, reporter, share);
            reportRepository.save(report);
        } catch (Exception e) {
            throw new ShareException(ShareErrorCode.SHARE_POSTING_FAILED);
        }
    }

    @Transactional
    public void completeShareSuccession(String authorizationHeader, ShareSuccessionRequestDTO request) {
        User giver = findUserFromHeader(authorizationHeader);
        User taker = userRepository.findByNickNameIsAndDeletedAtIsNull(request.getTakerNicName())
                .orElseThrow(() -> new ShareException(ShareErrorCode.SHARE_POSTING_FAILED));

        if (giver.getUserId().equals(taker.getUserId())) {
            throw new ShareException(ShareErrorCode.SHARE_BAD_REQUEST);
        }

        Share share = shareRepository.findById(request.getPostId())
                .orElseThrow(() -> new ShareException(ShareErrorCode.SHARE_POSTING_NOT_FOUND));

        if (!share.getUser().getUserId().equals(giver.getUserId())) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }

        UserIngredient giverIngredient = share.getUserIngredient();
        if (giverIngredient == null) {
            throw new ShareException(ShareErrorCode.USER_INGREDIENT_NOT_FOUND);
        }

        try {
            if ("전체 나눔".equals(request.getType())) {
                // Transfer ownership
                giverIngredient.updateUser(taker);
                userIngredientRepository.save(giverIngredient);
            } else if ("일부 나눔".equals(request.getType())) {
                // Clone ingredient for taker
                UserIngredient takerIngredient = UserIngredient.builder()
                        .user(taker)
                        .ingredient(giverIngredient.getIngredient())
                        .expirationDate(giverIngredient.getExpirationDate())
                        .status("INPUT")
                        .build();
                userIngredientRepository.save(takerIngredient);
            } else {
                throw new ShareException(ShareErrorCode.SHARE_BAD_REQUEST);
            }

            // Update share status
            share.setStatus("COMPLETED");
            shareRepository.save(share);
        } catch (ShareException e) {
            throw e;
        } catch (Exception e) {
            throw new ShareException(ShareErrorCode.SHARE_POSTING_FAILED);
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c * 10.0) / 10.0; // 1 decimal place
    }

    private static class ShareWithDistance {
        Share share;
        double distance;
        String displayAddress;

        ShareWithDistance(Share share, double distance, String displayAddress) {
            this.share = share;
            this.distance = distance;
            this.displayAddress = displayAddress;
        }
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
