package com.team200.graduation_project.domain.share.repository;

import com.team200.graduation_project.domain.share.entity.SharePicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SharePictureRepository extends JpaRepository<SharePicture, UUID> {

}
