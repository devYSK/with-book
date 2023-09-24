package com.ys.practice.data;

import org.springframework.data.repository.CrudRepository;

import com.ys.practice.domain.Taco;

public interface TacoRepository extends CrudRepository<Taco, Long> {
	
}