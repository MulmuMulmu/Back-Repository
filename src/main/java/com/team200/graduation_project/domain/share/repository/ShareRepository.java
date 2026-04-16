package com.team200.graduation_project.domain.share.repository;

import com.team200.graduation_project.domain.share.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShareRepository extends JpaRepository<Share, UUID> {

}
