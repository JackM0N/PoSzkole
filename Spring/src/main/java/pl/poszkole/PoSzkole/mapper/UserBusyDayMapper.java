package pl.poszkole.PoSzkole.mapper;

import org.mapstruct.*;
import pl.poszkole.PoSzkole.dto.UserBusyDayDTO;
import pl.poszkole.PoSzkole.model.UserBusyDay;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserBusyDayMapper {
    UserBusyDay toEntity(UserBusyDayDTO userBusyDayDTO);

    UserBusyDayDTO toDto(UserBusyDay userBusyDay);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserBusyDay partialUpdate(UserBusyDayDTO userBusyDayDTO, @MappingTarget UserBusyDay userBusyDay);
}