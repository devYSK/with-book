package com.ys.graphql.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.ys.graphql.model.Course;
import com.ys.graphql.model.Review;
import com.ys.graphql.repository.CourseRepository;
import com.ys.graphql.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class GraphqlCourseApiController {
	//
	// private final CourseRepository courseRepository;
	// private final ReviewRepository reviewRepository;
	//
	// @QueryMapping
	// Flux<Course> courses() {
	// 	return this.courseRepository.findAll();
	// }
	//
	// @QueryMapping
	// Flux<Review> reviews(@Argument Integer courseId) {
	// 	return this.reviewRepository.findByCourseId(courseId);
	// }
	//
	// @QueryMapping
	// Flux<Course> coursesByCategory(@Argument String category) {
	// 	return this.courseRepository.findByCategory(category);
	// }
	//
	// @SchemaMapping(typeName = "Course")
	// Flux<Review> reviews(Course course) {
	// 	return this.reviewRepository.findByCourseId(course.getId());
	// }
	//
	// @MutationMapping
	// Mono<Course> addCourse(@Argument String name, @Argument String category, @Argument String description) {
	// 	return this.courseRepository.save(new Course(null, name, category, description));
	// }
	//
	// @MutationMapping
	// Mono<Review> addReview(@Argument Integer courseId, @Argument String reviewerName, @Argument Integer rating,
	// 	@Argument String comment) {
	// 	return this.reviewRepository.save(new Review(null, courseId, reviewerName, rating, comment));
	// }
}
