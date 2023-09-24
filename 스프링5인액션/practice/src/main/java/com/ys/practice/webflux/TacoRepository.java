package com.ys.practice.webflux;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.ys.practice.domain.Taco;

public interface TacoRepository extends ReactiveCrudRepository<Taco, String> {

}