package com.ys.graphql.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.ys.graphql.model.Review;

import reactor.core.publisher.Flux;

@Repository
public interface ReviewRepository extends ReactiveCrudRepository<Review, Integer> {

    Flux<Review> findByCourseId(Integer courseId);
}
