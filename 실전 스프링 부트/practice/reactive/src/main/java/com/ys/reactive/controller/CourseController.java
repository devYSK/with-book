package com.ys.reactive.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ys.reactive.model.Course;
import com.ys.reactive.repository.CourseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

	private final CourseRepository courseRepository;

	@GetMapping
	public Flux<Course> getAllCourses() {
		System.out.println("good");
		System.out.println("good");

		return courseRepository.findAll();
	}

	@GetMapping("{id}")
	public Mono<ResponseEntity<Course>> getCourseById(@PathVariable("id") String courseId) {
		return courseRepository.findById(courseId)
							   .map(ResponseEntity::ok)
							   .defaultIfEmpty(ResponseEntity.notFound()
															 .build());
	}

	@GetMapping("category/{name}")
	public Flux<Course> getCourseByCategory(@PathVariable("name") String category) {
		return courseRepository.findAllByCategory(category)
							   .doOnError(e -> log.error("Failed to create course", e.getMessage()));
	}

	@PostMapping
	public Mono<Course> createCourse(@RequestBody Course course) {
		return courseRepository.save(course)
							   .doOnSuccess(updatedCourse -> log.info("Successfully created course", updatedCourse))
							   .doOnError(e -> log.error("Failed to create course", e.getMessage()));
	}

	@PutMapping("{id}")
	public Mono<ResponseEntity<Course>> updateCourse(@PathVariable("id") String courseId, @RequestBody Course course) {

		return this.courseRepository.findById(courseId)
									.flatMap(existingCourse -> {
										existingCourse.setName(course.getName());
										existingCourse.setRating(course.getRating());
										existingCourse.setCategory(course.getCategory());
										existingCourse.setDescription(course.getDescription());
										return this.courseRepository.save(existingCourse);
									})
									.map(ResponseEntity::ok)
									.defaultIfEmpty(ResponseEntity.notFound()
																  .build())
									.doOnError(e -> log.error("Failed to update course", e.getMessage()));
	}

	@DeleteMapping("{id}")
	public Mono<ResponseEntity<Course>> deleteCourseById(@PathVariable("id") String courseId) {
		return this.courseRepository.findById(courseId)
									.flatMap(
										course -> this.courseRepository.deleteById(course.getId())
																	   .then(Mono.just(ResponseEntity.ok(course))))
									.defaultIfEmpty(ResponseEntity.notFound()
																  .build());
	}

	@DeleteMapping
	public Mono<Void> deleteCourses() {
		return courseRepository.deleteAll();
	}

}
