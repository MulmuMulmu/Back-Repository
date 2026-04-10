package com.team200.graduation_project.domain.ingredient.repository;

import com.team200.graduation_project.domain.ingredient.entity.Ingredient;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    List<Ingredient> findTop10ByIngredientNameContaining(String keyword);

    Optional<Ingredient> findByIngredientName(String ingredientName);
}
