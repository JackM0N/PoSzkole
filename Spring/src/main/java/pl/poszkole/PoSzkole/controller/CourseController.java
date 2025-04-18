package pl.poszkole.PoSzkole.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.poszkole.PoSzkole.dto.*;
import pl.poszkole.PoSzkole.filter.CourseFilter;
import pl.poszkole.PoSzkole.service.CourseService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/not-started-courses")
    public ResponseEntity<Page<CourseDTO>> getAllNotStartedCourses(CourseFilter courseFilter, Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllNotStartedCourses(courseFilter, pageable));
    }

    @GetMapping("/available-courses")
    public ResponseEntity<Page<CourseDTO>> getAllAvailableCourses(CourseFilter courseFilter, Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllAvailableCourses(courseFilter, pageable));
    }

    @GetMapping("/bought-courses")
    public ResponseEntity<Page<CourseDTO>> getBoughtCourses(CourseFilter courseFilter, Pageable pageable) {
        return ResponseEntity.ok(courseService.getBoughtCourses(courseFilter, pageable));
    }

    @GetMapping("/active-courses")
    public ResponseEntity<Page<CourseDTO>> getActiveCourses(CourseFilter courseFilter, Pageable pageable) {
        return ResponseEntity.ok(courseService.getActiveCourses(courseFilter, pageable));
    }

    @GetMapping(value = "/description/{courseId}", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public ResponseEntity<String> getDescription(@PathVariable Long courseId) {
        String description = courseService.getCourseDescription(courseId);
        return ResponseEntity.ok().contentType(MediaType.valueOf("text/plain;charset=UTF-8")).body(description);
    }

    @GetMapping("/attendants/{courseId}")
    public ResponseEntity<List<SimplifiedUserDTO>> getAttendants(@PathVariable Long courseId){
        return ResponseEntity.ok(courseService.getCourseAttendants(courseId));
    }

    @PostMapping("/create")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        return ResponseEntity.ok(courseService.createCourse(courseDTO));
    }

    @PostMapping("/start-course")
    public ResponseEntity<CourseDTO> startCourse(@RequestBody StartCourseDTO startCourseDTO) {
        return ResponseEntity.ok(courseService.startCourse(startCourseDTO));
    }

    @PutMapping("/add-student")
    public ResponseEntity<CourseDTO> addStudent(@RequestBody StudentAndCourseDTO studentAndCourseDTO) {
        Long studentId = studentAndCourseDTO.getStudentId();
        Long courseId = studentAndCourseDTO.getCourseId();
        return ResponseEntity.ok(courseService.addStudentToCourse(courseId, studentId));
    }

    @PutMapping("/remove-student")
    public ResponseEntity<CourseDTO> removeStudent(@RequestBody StudentAndCourseDTO studentAndCourseDTO) {
        Long studentId = studentAndCourseDTO.getStudentId();
        Long courseId = studentAndCourseDTO.getCourseId();
        return ResponseEntity.ok(courseService.removeStudentFromCourse(studentId, courseId));
    }

    @PutMapping("/open/{courseId}")
    public ResponseEntity<CourseDTO> openCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.openCourseForRegistration(courseId));
    }

    @PutMapping("/finish/{courseId}")
    public ResponseEntity<CourseDTO> finishCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.finishCourse(courseId));
    }

    @PutMapping("/edit/{courseId}")
    public ResponseEntity<CourseDTO> editCourse(@PathVariable Long courseId, @RequestBody CourseDTO courseDTO) {
        return ResponseEntity.ok(courseService.editCourse(courseId, courseDTO));
    }

    @PutMapping("/cancel/{courseId}")
    public ResponseEntity<?> cancelCourse(@PathVariable Long courseId, @RequestBody ScheduleChangesLogDTO scheduleChangesLogDTO) {
        courseService.cancelCourse(courseId, scheduleChangesLogDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok().build();
    }
}
