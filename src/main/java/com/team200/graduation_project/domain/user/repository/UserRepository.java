package com.team200.graduation_project.domain.user.repository;

import com.team200.graduation_project.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByIdIs(String id);

    Optional<User> findByIdIs(String id);

    Optional<User> findByIdIsAndDeletedAtIsNull(String id);
}
