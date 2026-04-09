package com.team200.graduation_project.domain.share.repository;

import com.team200.graduation_project.domain.share.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}
