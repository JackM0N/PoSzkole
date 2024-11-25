package pl.poszkole.PoSzkole.mapper;

import org.mapstruct.*;
import pl.poszkole.PoSzkole.dto.ClassScheduleDTO;
import pl.poszkole.PoSzkole.model.ClassSchedule;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClassScheduleMapper {
    ClassSchedule toEntity(ClassScheduleDTO classScheduleDTO);

    ClassScheduleDTO toDto(ClassSchedule classSchedule);

    @Mapping(target = "tutoringClass.subject", ignore = true)
    @Mapping(target = "tutoringClass.teacher", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ClassSchedule partialUpdate(ClassScheduleDTO classScheduleDTO, @MappingTarget ClassSchedule classSchedule);
}