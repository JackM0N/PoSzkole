package pl.poszkole.PoSzkole.mapper;

import org.mapstruct.*;
import pl.poszkole.PoSzkole.dto.SubjectDTO;
import pl.poszkole.PoSzkole.model.Subject;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SubjectMapper {
    Subject toEntity(SubjectDTO subjectDTO);

    SubjectDTO toDto(Subject subject);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Subject partialUpdate(SubjectDTO subjectDTO, @MappingTarget Subject subject);
}