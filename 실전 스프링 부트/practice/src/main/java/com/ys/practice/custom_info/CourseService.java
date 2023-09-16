package com.ys.practice.custom_info;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ys.practice.register_user.Course;
import com.ys.practice.register_user.repository.CourseRepository;

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
