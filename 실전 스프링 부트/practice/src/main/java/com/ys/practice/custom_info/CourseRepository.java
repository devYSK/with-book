package com.ys.practice.custom_info;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ys.practice.fail.Course;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {

}
