package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.CourseDTO;
import pl.poszkole.PoSzkole.filter.CourseFilter;
import pl.poszkole.PoSzkole.mapper.CourseMapper;
import pl.poszkole.PoSzkole.model.Course;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.CourseRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final WebsiteUserService websiteUserService;
    private final WebsiteUserRepository websiteUserRepository;

    public Page<CourseDTO> getAllAvailableCourses(CourseFilter courseFilter, Pageable pageable) {
        Specification<Course> spec = applyCourseFilter(courseFilter);

        //Course has to be open for registration
        spec = spec.and(((root, query, builder) -> builder.equal(root.get("isOpenForRegistration"), true)));

        //Course cannot be done to be available
        spec = spec.and(((root, query, builder) -> builder.equal(root.get("isDone"), false)));

        //Course cannot have a class, because that would mean it has already started
        spec = spec.and(((root, query, builder) -> builder.isNull(root.get("tutoringClass"))));

        Page<Course> courses = courseRepository.findAll(spec, pageable);

        return courses.map(courseMapper::toDto);
    }

    public Page<CourseDTO> getBoughtCourses(CourseFilter courseFilter, Pageable pageable) {
        WebsiteUser currentUser = websiteUserService.getCurrentUser();

        // Initial specification to filter courses that the current user has already bought
        Specification<Course> spec = (root, query, builder) -> {
            // Join the Course with WebsiteUser's 'students' field
            Join<Course, WebsiteUser> studentsJoin = root.join("students");
            return builder.equal(studentsJoin.get("id"), currentUser.getId());
        };

        if (courseFilter != null) {
            spec = spec.and(applyCourseFilter(courseFilter));
        }

        Page<Course> courses = courseRepository.findAll(spec, pageable);

        return courses.map(courseMapper::toDto);
    }

    public Page<CourseDTO> getActiveCourses(CourseFilter courseFilter, Pageable pageable) {
        Specification<Course> spec = applyCourseFilter(courseFilter);

        //If course has a class that means its active
        spec = spec.and(((root, query, builder) -> builder.isNotNull(root.get("tutoringClass"))));

        //Course cannot be done to be active
        spec = spec.and(((root, query, builder) -> builder.equal(root.get("isDone"), false)));

        Page<Course> courses = courseRepository.findAll(spec, pageable);

        return courses.map(courseMapper::toDto);
    }

    //TODO: Add methods for: 1. Creating schedules for course and if needed add tutoring_class to course

    public CourseDTO createCourse(CourseDTO courseDTO) {
        Course course = courseMapper.toEntity(courseDTO);
        return courseMapper.toDto(courseRepository.save(course));
    }

    @Transactional
    public CourseDTO addStudentToCourse(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        WebsiteUser studentUser = websiteUserRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        //Check if it's actually a student
        if(studentUser.getRoles().stream().noneMatch(role -> "STUDENT".equals(role.getRoleName()))){
            throw new RuntimeException("User you are trying to add is not a student");
        }

        //Add student to chosen course
        studentUser.addCourse(course);

        websiteUserRepository.save(studentUser);
        return courseMapper.toDto(course);
    }

    public CourseDTO editCourse(Long courseId, CourseDTO courseDTO) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        courseMapper.partialUpdate(courseDTO, course);
        return courseMapper.toDto(courseRepository.save(course));
    }

    public CourseDTO openCourseForRegistration(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        course.setIsOpenForRegistration(true);
        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    public CourseDTO finishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        course.setIsDone(true);
        courseRepository.save(course);
        return courseMapper.toDto(course);
    }

    public void deleteCourse(Long courseId) {
        //TODO: Make sure there were no payments yet. This whole method might be useless. ASK
        courseRepository.deleteById(courseId);
    }

    //Method that prevents code repetition
    private Specification<Course> applyCourseFilter(CourseFilter courseFilter) {
        Specification<Course> spec = ((root, query, builder) -> builder.equal(root.get("isDone"), false));

        // Filter by course name if provided
        if (courseFilter.getName() != null) {
            String search = "%" + courseFilter.getName().toLowerCase() + "%";
            spec = spec.and((root, query, builder) -> builder.like(builder.lower(root.get("courseName")), search));
        }

        // Filter by price (less than or equal to the provided price)
        if (courseFilter.getPrice() != null) {
            spec = spec.and(((root, query, builder) -> builder.lessThanOrEqualTo(root.get("price"), courseFilter.getPrice())));
        }

        return spec;
    }
}
