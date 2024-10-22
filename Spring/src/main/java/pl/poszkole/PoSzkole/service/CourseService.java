package pl.poszkole.PoSzkole.service;

import jakarta.persistence.criteria.Join;
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

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final WebsiteUserService websiteUserService;

    //TODO: Ask if they should they exist until they are done or add Boolean started column to check if its started and finished one
    public Page<CourseDTO> getAllCourses(CourseFilter courseFilter, Pageable pageable) {
        Specification<Course> spec = applyCourseFilter(courseFilter);

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

    //TODO: Add method for buying said course or at least for reserving it (That might be a manager task to reserve or get confirmation about buying)

    public CourseDTO createCourse(CourseDTO courseDTO) {
        Course course = courseMapper.toEntity(courseDTO);
        return courseMapper.toDto(courseRepository.save(course));
    }

    public CourseDTO editCourse(Long courseId, CourseDTO courseDTO) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        courseMapper.partialUpdate(courseDTO, course);
        return courseMapper.toDto(courseRepository.save(course));
    }

    public void deleteCourse(Long courseId) {
        //TODO: Make sure there were no payments yet. This whole method might be useless. ASK
        courseRepository.deleteById(courseId);
    }

    //Method that prevents code repetition
    private Specification<Course> applyCourseFilter(CourseFilter courseFilter) {
        Specification<Course> spec = Specification.where(null);

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
