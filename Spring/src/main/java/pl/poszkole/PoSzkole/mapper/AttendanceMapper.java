package pl.poszkole.PoSzkole.mapper;

import org.mapstruct.*;
import pl.poszkole.PoSzkole.dto.AttendanceDTO;
import pl.poszkole.PoSzkole.model.Attendance;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AttendanceMapper {
    Attendance toEntity(AttendanceDTO attendanceDTO);

    AttendanceDTO toDto(Attendance attendance);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Attendance partialUpdate(AttendanceDTO attendanceDTO, @MappingTarget Attendance attendance);
}