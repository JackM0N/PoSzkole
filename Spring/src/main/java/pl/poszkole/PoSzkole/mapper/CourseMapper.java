package pl.poszkole.PoSzkole.mapper;

import org.mapstruct.*;
import pl.poszkole.PoSzkole.dto.CourseDTO;
import pl.poszkole.PoSzkole.model.Course;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseMapper {
    @Mapping(target = "students", ignore = true)
    Course toEntity(CourseDTO courseDTO);

    @Mapping(source = "tutoringClass.id", target = "tutoringClassId")
    CourseDTO toDto(Course course);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Course partialUpdate(CourseDTO courseDTO, @MappingTarget Course course);
}