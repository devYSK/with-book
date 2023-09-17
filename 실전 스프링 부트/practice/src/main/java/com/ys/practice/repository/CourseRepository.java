package com.ys.practice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ys.practice.entity.Course;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {

}
