package com.team200.graduation_project.domain.ingredient.repository;


import com.team200.graduation_project.domain.ingredient.entity.UserIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserIngredientRepository extends JpaRepository<UserIngredient, Long> {

}
