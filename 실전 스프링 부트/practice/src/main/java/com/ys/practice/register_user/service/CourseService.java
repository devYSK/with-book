package com.ys.practice.register_user.service;

import java.util.Optional;

import com.ys.practice.register_user.Course;

public interface CourseService {

    Course createCourse(Course course);

    Optional<Course> findCourseById(Long courseId);

    Iterable<Course> findAllCourses();

    Course updateCourse(Course course);

    void deleteCourseById(Long courseId);

}
