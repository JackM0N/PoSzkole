package pl.poszkole.PoSzkole.mapper;

import org.mapstruct.*;
import pl.poszkole.PoSzkole.dto.RoomReservationDTO;
import pl.poszkole.PoSzkole.model.RoomReservation;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {SimplifiedUserMapper.class})
public interface RoomReservationMapper {
    RoomReservation toEntity(RoomReservationDTO roomReservationDTO);

    RoomReservationDTO toDto(RoomReservation roomReservation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    RoomReservation partialUpdate(RoomReservationDTO roomReservationDTO, @MappingTarget RoomReservation roomReservation);
}