package com.team200.graduation_project.domain.recipe.repository;

import com.team200.graduation_project.domain.recipe.entity.RecipeStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {


}
