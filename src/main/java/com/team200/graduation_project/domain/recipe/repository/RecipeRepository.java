package com.team200.graduation_project.domain.recipe.repository;

import com.team200.graduation_project.domain.recipe.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

}
