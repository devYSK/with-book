package com.ys.practice.register_user.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ys.practice.register_user.Course;
import com.ys.practice.register_user.repository.CourseRepository;
import com.ys.practice.register_user.service.CourseService;

@Service
public class DefaultCourseService implements CourseService {

    private final CourseRepository courseRepository;

    public DefaultCourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Optional<Course> findCourseById(Long courseId) {
        return courseRepository.findById(courseId);
    }

    public Iterable<Course> findAllCourses() {
        return courseRepository.findAll();
    }

    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourseById(Long courseId) {
        courseRepository.deleteById(courseId);
    }
}
