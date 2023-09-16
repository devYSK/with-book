package com.ys.practice.register_user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ys.practice.register_user.Course;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {

}
