package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.CourseDTO;
import pl.poszkole.PoSzkole.filter.CourseFilter;
import pl.poszkole.PoSzkole.service.CourseService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/list")
    public ResponseEntity<Page<CourseDTO>> getAllCourses(CourseFilter courseFilter, Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCourses(courseFilter, pageable));
    }

    @GetMapping("/bought-courses")
    public ResponseEntity<Page<CourseDTO>> getBoughtCourses(CourseFilter courseFilter, Pageable pageable) {
        return ResponseEntity.ok(courseService.getBoughtCourses(courseFilter, pageable));
    }

    @PostMapping("/create")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        return ResponseEntity.ok(courseService.createCourse(courseDTO));
    }

    @PutMapping("/edit/{courseId}")
    public ResponseEntity<CourseDTO> editCourse(@PathVariable Long courseId, @RequestBody CourseDTO courseDTO) {
        return ResponseEntity.ok(courseService.editCourse(courseId, courseDTO));
    }

    @DeleteMapping("/delete/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok().build();
    }
 }
