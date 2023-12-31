package com.ys.practice.custom.custom_info;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ys.practice.entity.Course;
import com.ys.practice.repository.CourseRepository;

@Service
public class CourseService {

    private CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Iterable<Course> getAvailableCourses() {
        return courseRepository.findAll();
    }
}
