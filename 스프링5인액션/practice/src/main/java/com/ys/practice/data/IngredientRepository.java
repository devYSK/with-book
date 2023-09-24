package com.ys.practice.data;

import org.springframework.data.repository.CrudRepository;

import com.ys.practice.domain.Ingredient;

public interface IngredientRepository extends CrudRepository<Ingredient, String> {
	
}