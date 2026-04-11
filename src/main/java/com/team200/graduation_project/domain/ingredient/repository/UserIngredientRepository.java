package com.team200.graduation_project.domain.ingredient.repository;


import com.team200.graduation_project.domain.ingredient.entity.UserIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team200.graduation_project.domain.user.entity.User;
import org.springframework.data.domain.Sort;

import java.util.List;

@Repository
public interface UserIngredientRepository extends JpaRepository<UserIngredient, Long> {
    List<UserIngredient> findByUserAndIngredient_CategoryIn(User user, List<String> categories, Sort sort);
    List<UserIngredient> findByUser(User user, Sort sort);
    int countByUserAndExpirationDateBetween(User user, java.time.LocalDate startDate, java.time.LocalDate endDate);
    int countByUserAndExpirationDateLessThanEqual(User user, java.time.LocalDate date);
}
