package com.ys.graphql.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.ys.graphql.model.Course;

import reactor.core.publisher.Flux;


@Repository
public interface CourseRepository extends ReactiveCrudRepository<Course, Integer>  {

    Flux<Course> findByCategory(String category);
}
