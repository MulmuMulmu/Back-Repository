package com.team200.graduation_project.domain.share.repository;

import com.team200.graduation_project.domain.share.entity.Share;
import com.team200.graduation_project.domain.share.entity.ShareStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShareRepository extends JpaRepository<Share, UUID> {

    @Query("SELECT s FROM Share s JOIN FETCH s.user LEFT JOIN FETCH s.sharePicture WHERE s.deletedAt IS NULL")
    List<Share> findAllWithUser();

    @Query("SELECT s FROM Share s JOIN FETCH s.user LEFT JOIN FETCH s.sharePicture WHERE s.shareId = :shareId AND s.deletedAt IS NULL")
    java.util.Optional<Share> findWithUserByShareId(UUID shareId);

    @Query("SELECT s FROM Share s LEFT JOIN FETCH s.sharePicture WHERE s.user = :user AND s.status = :status AND s.deletedAt IS NULL ORDER BY s.createTime DESC")
    List<Share> findAllByUserAndStatusOrderByCreateTimeDesc(com.team200.graduation_project.domain.user.entity.User user, ShareStatus status);

    long countByCreateTimeBetween(LocalDateTime start, LocalDateTime end);

    long countByUserAndDeletedAtIsNull(com.team200.graduation_project.domain.user.entity.User user);
}
