package pl.poszkole.PoSzkole.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.poszkole.PoSzkole.dto.*;
import pl.poszkole.PoSzkole.filter.CourseFilter;
import pl.poszkole.PoSzkole.mapper.CourseMapper;
import pl.poszkole.PoSzkole.mapper.SimplifiedUserMapper;
import pl.poszkole.PoSzkole.mapper.TutoringClassMapper;
import pl.poszkole.PoSzkole.model.ClassSchedule;
import pl.poszkole.PoSzkole.model.Course;
import pl.poszkole.PoSzkole.model.TutoringClass;
import pl.poszkole.PoSzkole.model.WebsiteUser;
import pl.poszkole.PoSzkole.repository.ClassScheduleRepository;
import pl.poszkole.PoSzkole.repository.CourseRepository;
import pl.poszkole.PoSzkole.repository.TutoringClassRepository;
import pl.poszkole.PoSzkole.repository.WebsiteUserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final WebsiteUserService websiteUserService;
    private final WebsiteUserRepository websiteUserRepository;
    private final TutoringClassMapper tutoringClassMapper;
    private final TutoringClassRepository tutoringClassRepository;
    private final ClassScheduleService classScheduleService;
    private final SimplifiedUserMapper simplifiedUserMapper;
    private final TutoringClassService tutoringClassService;
    private final ClassScheduleRepository classScheduleRepository;

    public Page<CourseDTO> getAllNotStartedCourses(CourseFilter courseFilter, Pageable pageable) {
        Specification<Course> spec = applyCourseFilter(courseFilter);

        //Course cannot have a class, because that would mean it has already started
        spec = spec.and(((root, query, builder) -> builder.isNull(root.get("tutoringClass"))));

        //Course cannot be done
        spec = spec.and(((root, query, builder) -> builder.equal(root.get("isDone"), false)));

        Page<Course> courses = courseRepository.findAll(spec, pageable);

        return courses.map(courseMapper::toDto);
    }

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

        return courses.map(course -> {
            ClassSchedule classSchedule = classScheduleRepository.findLastScheduleByClassId(course.getTutoringClass().getId())
                    .orElse(null);
            CourseDTO courseDTO = courseMapper.toDto(course);
            if(classSchedule != null) {
                courseDTO.setTeacher(simplifiedUserMapper.toSimplifiedUserDTO(classSchedule.getTutoringClass().getTeacher()));
                courseDTO.setLastScheduleDate(classSchedule.getClassDateFrom());
            }
            return courseDTO;
        });
    }

    public String getCourseDescription(Long courseId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        return course.getDescription();
    }

    public List<SimplifiedUserDTO> getCourseAttendants(Long courseId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        List<WebsiteUser> attendants = course.getStudents();

        return attendants.stream().map(simplifiedUserMapper::toSimplifiedUserDTO).collect(Collectors.toList());
    }

    @Transactional
    public CourseDTO startCourse(StartCourseDTO startCourseDTO) {
        //Get course
        Course course = courseRepository.findById(startCourseDTO.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        List<WebsiteUser> students = course.getStudents();

        //Find wanted teacher
        WebsiteUser teacher = websiteUserRepository.findById(startCourseDTO.getTeacherId())
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

        if (teacher.getRoles().stream().noneMatch(role -> "TEACHER".equals(role.getRoleName()))) {
            throw new EntityNotFoundException("Chosen user is not a teacher");
        }

        //Create class
        TutoringClass tutoringClass = tutoringClassMapper.toEntity(startCourseDTO.getTutoringClassDTO());
        tutoringClass.setTeacher(teacher);
        tutoringClassRepository.save(tutoringClass);

        //Add students to created class
        students.forEach(studentUser -> {
            studentUser.addClass(tutoringClass);
            websiteUserRepository.save(studentUser);
        });

        classScheduleService.createRepeatingClassSchedule(
                startCourseDTO.getDayAndTimeDTO(),
                tutoringClass,
                startCourseDTO.getIsOnline(),
                startCourseDTO.getRepeatUntil(),
                students
        );

        course.setTutoringClass(tutoringClass);

        courseRepository.save(course);

        return courseMapper.toDto(course);
    }

    public CourseDTO createCourse(CourseDTO courseDTO) {
        Course course = courseMapper.toEntity(courseDTO);
        return courseMapper.toDto(courseRepository.save(course));
    }

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

        if (course.getStudents().size() == course.getMaxParticipants()){
            course.setIsOpenForRegistration(false);
            courseRepository.save(course);
        }

        websiteUserRepository.save(studentUser);
        return courseMapper.toDto(course);
    }

    public CourseDTO removeStudentFromCourse(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        WebsiteUser studentUser = websiteUserRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        //Remove student from the chosen course
        studentUser.removeCourse(course);

        websiteUserRepository.save(studentUser);
        return courseMapper.toDto(course);
    }

    public CourseDTO editCourse(Long courseId, CourseDTO courseDTO) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        courseMapper.partialUpdate(courseDTO, course);
        courseRepository.save(course);
        return courseMapper.toDto(course);
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

    @Transactional
    public void cancelCourse(Long courseId, ScheduleChangesLogDTO scheduleChangesLogDTO) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));
        //Mark course as done
        course.setIsDone(true);
        //Cancel the rest of its schedules
        tutoringClassService.cancelTheRestOfTutoringClass(course.getTutoringClass().getId(), scheduleChangesLogDTO);

        courseRepository.save(course);
    }

    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        if(course.getIsOpenForRegistration()){
            throw new RuntimeException("You cant delete a course that is already open for registration");
        }

        if(!course.getStudents().isEmpty()){
            throw new RuntimeException("You cant delete a course that people are already registered to");
        }

        courseRepository.delete(course);
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
