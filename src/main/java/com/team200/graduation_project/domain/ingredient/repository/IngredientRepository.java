package com.team200.graduation_project.domain.ingredient.repository;

import com.team200.graduation_project.domain.ingredient.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

}
