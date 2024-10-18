package pl.poszkole.PoSzkole.mapper;

import org.mapstruct.*;
import pl.poszkole.PoSzkole.dto.ScheduleChangesLogDTO;
import pl.poszkole.PoSzkole.model.ScheduleChangesLog;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ScheduleChangesLogMapper {
    ScheduleChangesLog toEntity(ScheduleChangesLogDTO scheduleChangesLogDTO);

    ScheduleChangesLogDTO toDto(ScheduleChangesLog scheduleChangesLog);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ScheduleChangesLog partialUpdate(ScheduleChangesLogDTO scheduleChangesLogDTO, @MappingTarget ScheduleChangesLog scheduleChangesLog);
}